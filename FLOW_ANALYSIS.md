# Bus Management System - Complete Flow Analysis

## 1. PARENT REGISTRATION/CONFIRMATION FLOW

### 1.1 Parent Registration Entry Point: `ParentRegistrationServlet`

**GET Request** (`/parent/registration`):
- Loads the registration page with:
  - List of students belonging to the parent (via `StudentDAO.getStudentsByParent()`)
  - Morning and afternoon registration status for each student
  - Manifest status (trip confirmation info) from `ManifestDAO.getStudentTripStatus()`
  - Permission flags to edit based on time rules (`TimeRuleUtil.canParentEditMorning/Afternoon()`)
  - Time hints for the parent

**POST Request** (Parent submits registration/update):
```java
// Parameters received:
- studentId: Which student is being registered
- sessionType: "MORNING" or "AFTERNOON"
- attendanceChoice: "BUS" (going by bus) or "WALK" (not using bus)
- note: Optional notes from parent
```

**Flow Logic**:
1. Get current demo time via `DemoTimeUtil.getDemoTime(session)`
2. Check if time window allows editing:
   - Morning edit window: **6 PM - 5 AM** (captured by `canParentEditMorning()`)
   - Afternoon edit window: **Until 2 PM OR 6 PM onwards** (captured by `canParentEditAfternoon()`)
3. If time window is VALID:
   - Call `RegistrationDAO.saveOrUpdateRegistration()` with parameters
   - Save to `DailyTripRegistration` table (INSERT or UPDATE)
   - Set success message
4. If time window is LOCKED:
   - Display error: "Time window does not allow edits"
   - Redirect back to registration page

**Database Interaction** (`RegistrationDAO.saveOrUpdateRegistration()`):
```sql
-- Check if registration exists:
SELECT * FROM DailyTripRegistration 
WHERE StudentID = ? AND TripDate = ? AND SessionType = ?

-- If NOT EXISTS - INSERT:
INSERT INTO DailyTripRegistration 
  (StudentID, TripDate, SessionType, AttendanceChoice, SourceType, Note, UpdatedAt)
VALUES (?, ?, ?, ?, 'PARENT', ?, GETDATE())

-- If EXISTS - UPDATE:
UPDATE DailyTripRegistration 
SET AttendanceChoice = ?, SourceType = 'PARENT', Note = ?, UpdatedAt = GETDATE()
WHERE RegistrationID = ?
```

### 1.2 Parent Dashboard View: `ParentDashboardServlet`

Similar to registration servlet but READ-ONLY:
- Shows student list and their registration statuses
- Shows trip manifest status (real-time confirmation)
- Displays time hints about registration windows
- Does NOT process edits (viewing only)

---

## 2. LOCK TIME MECHANISM

### 2.1 Time Rules Definition: `TimeRuleUtil`

```java
// MORNING SESSION RULES:
public static boolean canParentEditMorning(LocalTime time) {
    return !time.isBefore(LocalTime.of(18, 0)) ||  // >= 18:00 (6 PM)
           time.isBefore(LocalTime.of(5, 0));       // < 05:00 (5 AM)
}
// Parent can edit: 6 PM to 5 AM

public static boolean isMorningLocked(LocalTime time) {
    return !time.isBefore(LocalTime.of(5, 0)) &&   // >= 05:00 (5 AM)
           time.isBefore(LocalTime.of(7, 0));      // < 07:00 (7 AM)
}
// LOCKED: 5 AM to 7 AM (manifest finalization time)

public static boolean isMorningRunning(LocalTime time) {
    return !time.isBefore(LocalTime.of(7, 0)) &&   // >= 07:00 (7 AM)
           time.isBefore(LocalTime.of(14, 0));    // < 14:00 (2 PM)
}
// RUNNING: 7 AM to 2 PM (bus operation time)

// AFTERNOON SESSION RULES:
public static boolean canParentEditAfternoon(LocalTime time) {
    return !time.isBefore(LocalTime.of(18, 0)) ||  // >= 18:00 (6 PM)
           time.isBefore(LocalTime.of(14, 0));     // < 14:00 (2 PM)
}
// Parent can edit: Until 2 PM OR 6 PM onwards

public static boolean isAfternoonLocked(LocalTime time) {
    return !time.isBefore(LocalTime.of(14, 0)) &&  // >= 14:00 (2 PM)
           time.isBefore(LocalTime.of(16, 0));    // < 16:00 (4 PM)
}
// LOCKED: 2 PM to 4 PM (manifest finalization time)

public static boolean isAfternoonRunning(LocalTime time) {
    return !time.isBefore(LocalTime.of(16, 0)) &&  // >= 16:00 (4 PM)
           time.isBefore(LocalTime.of(18, 0));    // < 18:00 (6 PM)
}
// RUNNING: 4 PM to 6 PM (bus operation time)

public static boolean canManagerUpdateManifest(String sessionType, LocalTime time) {
    if ("MORNING".equalsIgnoreCase(sessionType)) {
        return isMorningRunning(time);  // Only 7 AM - 2 PM
    }
    if ("AFTERNOON".equalsIgnoreCase(sessionType)) {
        return isAfternoonRunning(time); // Only 4 PM - 6 PM
    }
    return false;
}
```

### 2.2 Time Simulation: `DemoTimeUtil`

- **Default Time**: 04:30 (4:30 AM)
- **Storage**: Application-wide (stored in `ServletContext`)
- **Shared**: Single demo time for ALL users in the application
- **Functions**:
  - `getDemoTime(ServletContext)` - Get current simulated time
  - `setDemoTime(ServletContext, LocalTime)` - Set new time (for admin/testing)
  - `resetDemoTime(ServletContext)` - Reset to default 04:30

### 2.3 Time Flow Timeline

```
MORNING SESSION:
6 PM (18:00) ────────────── CAN EDIT (Parent) ─────────── 5 AM (05:00)
5 AM (05:00) ───── LOCKED (Manifest Finalization) ───── 7 AM (07:00)
7 AM (07:00) ─────── RUNNING (Trip In Progress) ─────── 2 PM (14:00)

AFTERNOON SESSION:
Until 2 PM ─── CAN EDIT ─────────────────────────────────────────
2 PM (14:00) ──── LOCKED (Manifest Finalization) ────── 4 PM (16:00)
4 PM (16:00) ──── RUNNING (Trip In Progress) ────────── 6 PM (18:00)
6 PM onwards ─── CAN EDIT ─────────────────────────────────────────
```

---

## 3. MANIFEST CREATION & GENERATION

### 3.1 Data Structure

**TripManifest** (Trip-level grouping):
```
ManifestID (PK)
├── AssignmentID → BusAssignment
│   ├── ManagerUserID
│   ├── DriverUserID
│   ├── BusID → Bus (PlateNumber, BusName, Capacity)
│   └── RouteID → Route (RouteName)
├── TripDate (e.g., 2025-03-21)
├── SessionType ("MORNING" or "AFTERNOON")
├── ManifestStatus ("PENDING", "READY", "RUNNING", "COMPLETED")
├── CurrentRouteStopID → Current location during trip
├── DepartureTime (SQL TIME)
├── StartedAt (When trip began)
└── FinishedAt (When trip ended)
```

**ManifestStudent** (Student records within a manifest):
```
ManifestStudentID (PK)
├── ManifestID (FK) → TripManifest
├── StudentID (FK) → Student
├── AttendanceChoice ("BUS" or "WALK" from registration)
├── BoardingStatus ("PENDING", "BOARDED", or "NO-SHOW")
├── BoardedAt (When student boarded)
└── Note
```

### 3.2 Critical Finding: **MANIFEST CREATION IS NOT IMPLEMENTED IN JAVA CODE**

⚠️ **MAJOR GAP**: The application has NO code that:
- Creates TripManifest records
- Creates ManifestStudent records from DailyTripRegistration
- Triggers manifest generation after parent registration deadline

**What EXISTS:**
- ManifestDAO only has READ and UPDATE operations
- No `createManifest()`, `generateManifest()`, or `createManifestFromRegistrations()` methods
- No scheduled jobs or event listeners

**Implications:**
- Manifests must be created manually in the database OR
- There's an external batch process/stored procedure (not in this Java code) OR
- This is a design gap that needs implementation

### 3.3 How Manifests Are USED (Current Implementation)

**Reading Manifests** (`ManifestDAO.getManifestByManager()`, `getManifestByDriver()`):
```sql
SELECT tm.ManifestID, tm.AssignmentID, tm.TripDate, tm.SessionType,
       tm.ManifestStatus, tm.CurrentRouteStopID, tm.DepartureTime,
       b.PlateNumber, r.RouteName, ...
FROM TripManifest tm
INNER JOIN BusAssignment ba ON tm.AssignmentID = ba.AssignmentID
INNER JOIN Bus b ON ba.BusID = b.BusID
INNER JOIN Route r ON ba.RouteID = r.RouteID
WHERE (ba.ManagerUserID = ? OR ba.DriverUserID = ?) 
  AND tm.TripDate = ? AND tm.SessionType = ?
```

**Getting Students in Manifest** (`ManifestDAO.getManifestStudents()`):
```sql
SELECT ms.ManifestStudentID, ms.StudentID, ms.AttendanceChoice,
       ms.BoardingStatus, ms.BoardedAt, s.StudentCode, s.FullName,
       sp.StopName AS PickupStopName
FROM ManifestStudent ms
INNER JOIN Student s ON ms.StudentID = s.StudentID
INNER JOIN StopPoint sp ON s.DefaultPickupStopID = sp.StopID
WHERE ms.ManifestID = ?
ORDER BY sp.StopName, s.FullName
```

**Updating During Trip** (`ManifestDAO.updateBoardingStatus()`):
```sql
UPDATE ManifestStudent
SET BoardingStatus = ?,
    BoardedAt = CASE WHEN ? = 'BOARDED' THEN GETDATE() ELSE NULL END
WHERE ManifestStudentID = ?
```

---

## 4. MANAGER MANIFEST RECEPTION

### 4.1 Manager Views Manifest: `ManagerManifestServlet`

**GET Request** (`/manager/manifest`):
```
1. Get current user (manager)
2. Get today's date
3. Get demo time
4. Get sessionType parameter (or default to preferred session)
5. Query: ManifestDAO.getManifestByManager(managerUserId, today, sessionType)
6. If manifest exists, get students: ManifestDAO.getManifestStudents(manifestId)
7. Forward to JSP with:
   - manifest (TripManifest object)
   - students (List<ManifestStudent>)
   - canUpdate (boolean from TimeRuleUtil.canManagerUpdateManifest)
```

**Manager Can View Manifest When:**
- A TripManifest record exists for their assigned bus/route
- Today's date matches the manifest's TripDate
- SessionType matches

### 4.2 Manager Updates Boarding: `ManagerBoardingServlet`

**POST Request** (`/manager/boarding`):
```
1. Verify time window allows updates:
   - canManagerUpdateManifest(sessionType, demoTime) must be TRUE
   - Only during RUNNING time (7 AM-2 PM for morning, 4 PM-6 PM for afternoon)
2. Update boarding status for a student:
   - ManifestDAO.updateBoardingStatus(manifestStudentId, boardingStatus)
   - Sets BoardedAt to GETDATE() if status is 'BOARDED'
3. Redirect back to boarding page with success/error message
```

### 4.3 Manager Dashboard: `ManagerDashboardServlet`

Displays:
- Bus assignment details
- Manifest for the session
- List of students with boarding status
- Counts:
  - Total students taking bus (AttendanceChoice = 'BUS')
  - Boarded students (BoardingStatus = 'BOARDED')
  - Pending students (BoardingStatus = 'PENDING')
- Update permission flag

---

## 5. KEY BUSINESS RULES & FLOW CONNECTIONS

### 5.1 Complete Flow from Parent Confirmation to Manager Manifest

```
COMPLETE DAILY TRIP FLOW:

【6:00 PM (18:00) Previous Day to 5:00 AM】
└─ PARENT REGISTRATION WINDOW (canParentEditMorning = TRUE)
   ├─ Parent logs in (ParentRegistrationServlet / ParentDashboardServlet)
   ├─ Reviews their children
   ├─ For each child, chooses:
   │  ├─ sessionType: MORNING or AFTERNOON
   │  ├─ attendanceChoice: BUS or WALK
   │  └─ optional note
   ├─ Submits registration
   └─ Data saved to DailyTripRegistration table
      ├─ StudentID
      ├─ TripDate
      ├─ SessionType
      ├─ AttendanceChoice
      ├─ SourceType = "PARENT"
      └─ UpdatedAt = current timestamp

【5:00 AM - 7:00 AM】
└─ MANIFEST FINALIZATION WINDOW (isMorningLocked = TRUE)
   ├─ ⚠️ SYSTEM SHOULD: Generate manifest from DailyTripRegistration records
   │  │   (THIS IS NOT IMPLEMENTED IN THE JAVA CODE)
   │  └─ Create TripManifest record with:
   │     ├─ AssignmentID (from BusAssignment)
   │     ├─ TripDate = today
   │     ├─ SessionType = "MORNING"
   │     ├─ ManifestStatus = "READY"
   │     └─ DepartureTime = route's scheduled time
   │
   │   Create ManifestStudent records from matching DailyTripRegistrations:
   │     ├─ For each reg with AttendanceChoice = "BUS"
   │     ├─ ManifestID = newly created manifest
   │     ├─ StudentID from registration
   │     ├─ AttendanceChoice = "BUS"
   │     └─ BoardingStatus = "PENDING"
   │
   ├─ Parent CANNOT edit (ParentRegistrationServlet rejects with error)
   └─ Manager can start seeing the manifest

【7:00 AM - 2:00 PM】
└─ TRIP RUNNING WINDOW (isMorningRunning = TRUE)
   ├─ Manager views manifest (ManagerManifestServlet, ManagerBoardingServlet)
   ├─ Manager marks students as BOARDED (ManagerBoardingServlet POST)
   │  ├─ ManifestStudent.BoardingStatus = "BOARDED"
   │  └─ ManifestStudent.BoardedAt = GETDATE()
   ├─ Driver navigates trip (DriverDashboardServlet views manifest)
   ├─ ManifestStatus progresses:
   │  ├─ Initially: "READY"
   │  ├─ When started: "RUNNING"
   │  └─ When completed: "COMPLETED"
   └─ CurrentRouteStopID updated as driver progresses

【2:00 PM (14:00) onwards】
└─ Trip completed for morning session
   └─ Parent can see trip status (ParentTripStatusServlet)
      └─ ManifestDAO.getStudentTripStatus() shows:
         ├─ If their child was on the bus
         ├─ Boarding status
         ├─ Current stop (if still running)
         └─ Last update time
```

### 5.2 Attendance Choice Impact

**Key Rule**: Only students with `AttendanceChoice = "BUS"` appear in manifest

```
Parent Registration Form:
├─ BUS Selection → DailyTripRegistration.AttendanceChoice = "BUS"
│  ├─ Links to manifest creation
│  ├─ Appears in ManifestStudent
│  └─ Manager tracks boarding status
│
└─ WALK Selection → DailyTripRegistration.AttendanceChoice = "WALK"
   ├─ NOT included in manifest
   ├─ No ManifestStudent record
   └─ Not tracked in manager boarding
```

### 5.3 Time Rule Impact on Operations

```
MORNING SESSION TIMELINE:

✓ 6 PM - 5 AM: Parent Can Edit (canParentEditMorning = TRUE)
              │ Can register, change choice, add notes
              │
✗ 5 AM - 7 AM: Locked (isMorningLocked = TRUE)
              │ Parent CANNOT edit
              │ ⚠️ Manifest should be generated HERE
              │
✓ 7 AM - 2 PM: Running (isMorningRunning = TRUE)
              │ Manager CAN update boarding (canManagerUpdateManifest = TRUE)
              │ Driver drives the bus
              │
✗ 2 PM - 6 PM: No operations
              │ Parent CANNOT edit afternoon yet
              │ Manager CANNOT update

AFTERNOON SESSION TIMELINE:

✓ Until 2 PM: Parent Can Edit
            │
✗ 2 PM - 4 PM: Locked (isAfternoonLocked = TRUE)
            │ Manifest should be generated HERE
            │
✓ 4 PM - 6 PM: Running (isAfternoonRunning = TRUE)
            │ Manager CAN update boarding
            │
✓ 6 PM - 2 PM next: Parent Can Edit
```

---

## 6. MANIFEST STATUS TRANSITIONS

```
Database → ManifestStatus field values:

"PENDING" → Initial state (pre-generated or auto-generated)
    ↓
"READY" → Manifest finalized, ready for driver
    ↓
"RUNNING" → Trip in progress
    ├─ StartedAt = first update time
    ├─ CurrentRouteStopID updates as driver progresses
    └─ ManifestStudent.BoardingStatus changes from PENDING → BOARDED
    ↓
"COMPLETED" → Trip finished
    └─ FinishedAt = completion timestamp
```

---

## 7. DATA FLOW DIAGRAM

```
USER INTERACTIONS:
───────────────────

PARENT (6 PM - 5 AM window for MORNING, until 2 PM for AFTERNOON)
  ↓
ParentRegistrationServlet.doPost()
  ├─ Validates time window (TimeRuleUtil.canParentEditMorning/Afternoon)
  ├─ Calls RegistrationDAO.saveOrUpdateRegistration()
  └─ Stores to DailyTripRegistration
       (StudentID, TripDate, SessionType, AttendanceChoice, SourceType="PARENT")


【DATABASE TRANSFORMATION - NOT IN JAVA CODE】
DailyTripRegistration → [MISSING: Manifest Generation] → TripManifest + ManifestStudent


MANAGER/DRIVER (7 AM - 2 PM for MORNING, 4 PM - 6 PM for AFTERNOON)
  ↓
ManagerManifestServlet.doGet() / ManagerBoardingServlet
  ├─ ManifestDAO.getManifestByManager/Driver()
  ├─ ManifestDAO.getManifestStudents()
  └─ Displays manifest with students


MANAGER BOARDING UPDATE
  ↓
ManagerBoardingServlet.doPost()
  ├─ Validates time window (TimeRuleUtil.canManagerUpdateManifest)
  ├─ Calls ManifestDAO.updateBoardingStatus()
  └─ Updates ManifestStudent.BoardingStatus
```

---

## 8. WHERE MANIFESTS ARE CREATED/UPDATED IN DATABASE

### 8.1 In This Java Application: ❌ NOT IMPLEMENTED

No code creates or generates manifests from registrations.

### 8.2 Assumed External Mechanism:

Option A: **SQL Stored Procedure** (Candidate: `sp_GenerateDailyManifests`)
```sql
-- Pseudo-code for what SHOULD be implemented
PROCEDURE sp_GenerateDailyManifests @TripDate DATE, @SessionType NVARCHAR(20)
  ├─ For each active BusAssignment
  ├─ Create TripManifest
  ├─ Query DailyTripRegistration WHERE AttendanceChoice='BUS'
  ├─ Create corresponding ManifestStudent entries
  └─ Set ManifestStatus = 'READY'
```

Option B: **Separate Batch Job** (Not in codebase)
- Runs at 5 AM for MORNING session
- Runs at 2 PM for AFTERNOON session
- Generates manifests automatically

Option C: **Manual Admin Process**
- Admin manually creates manifests
- System queries pre-created manifests
- Less automated, more error-prone

### 8.3 How Manifests Are UPDATED:

✓ `ManifestDAO.updateBoardingStatus()` - When manager marks student as boarded
✓ `ManifestDAO.updateCurrentRouteStop()` - When driver moves to next stop
✓ Direct SQL updates (not shown in Java code)

---

## 9. POTENTIAL GAPS & ISSUES

### 9.1 Critical Issues

| Issue | Severity | Impact |
|-------|----------|--------|
| **No manifest creation logic** | 🔴 CRITICAL | System cannot function - managers have nothing to view |
| **No automated manifest generation** | 🔴 CRITICAL | Requires manual intervention or external process |
| **No link between registration and manifest** | 🔴 CRITICAL | Parent registrations don't feed into operational manifests |
| **Missing TransactionIsolation** | 🟡 HIGH | Race conditions if multiple operations occur simultaneously |

### 9.2 Logic Flow Issues

| Issue | Severity | Impact |
|-------|----------|--------|
| **Demo time shared application-wide** | 🟡 HIGH | All users see same simulated time - limits testing realism |
| **Manifest lock times hardcoded** | 🟡 MEDIUM | Times cannot be configured without code changes |
| **No validation of BusAssignment** | 🟠 MEDIUM | If no bus assigned to manager, manifest is null |
| **No error recovery** | 🟠 MEDIUM | If manifest generation fails, no retry mechanism |

### 9.3 Data Consistency Issues

| Issue | Severity | Impact |
|-------|----------|--------|
| **Orphaned ManifestStudent records** | 🟡 MEDIUM | If parent changes choice after manifest created |
| **No manifest status validation** | 🟠 MEDIUM | Can update boarding on completed trips |
| **No capacity checks** | 🟠 MEDIUM | Bus capacity not validated against registered students |
| **No rollback mechanism** | 🟡 MEDIUM | Failed updates leave incomplete state |

### 9.4 Specific Logic Gaps

```
MISSING FUNCTIONALITY:

1. Manifest Generation Trigger
   - No code to create manifest after 5 AM lock time
   - No code to populate ManifestStudent from registrations
   - No status transition from "PENDING" → "READY"

2. Parent Confirmation Linkage
   - DailyTripRegistration.AttendanceChoice not synced to manifest
   - If parent changes from BUS → WALK after manifest created, no update
   - If parent changes WALK → BUS after manifest created, no update

3. Manifest Validation
   - No check if BusAssignment exists
   - No check if route matches student's default route
   - No validation of pickup stop consistency

4. Trip State Management
   - No automatic status transitions
   - No completion/finalization logic
   - No incident linking to manifest state

5. Boarding Audit Trail
   - No history of boarding changes
   - No who/when tracking for updates
   - No rollback capability for boarding changes
```

---

## 10. SUMMARY TABLE: PARENT CONFIRMATION → MANAGER MANIFEST

| Stage | Timing | Component | Operation | Data Changed |
|-------|--------|-----------|-----------|--------------|
| **Parent Registers** | 6 PM - 5 AM (M), Until 2 PM (A) | ParentRegistrationServlet | INSERT/UPDATE | DailyTripRegistration |
| **Lock Time** | 5-7 AM (M), 2-4 PM (A) | TimeRuleUtil | Blocks parent edit, parent view only | None (ideally generate manifest) |
| **[MISSING] Manifest Gen** | *Should happen at lock time* | *No Java code* | *Should create manifest* | *TripManifest, ManifestStudent* |
| **Trip Running** | 7 AM-2 PM (M), 4-6 PM (A) | ManagerManifestServlet | Manager views, updates boarding | ManifestStudent.BoardingStatus |
| **Parent Views Status** | Anytime | ParentTripStatusServlet | Read manifest data | None |
| **Trip Ends** | After 2 PM (M), After 6 PM (A) | ManifestDAO (auto?) | Mark completed | TripManifest.FinishedAt |

---

## 11. CONDITIONS FOR MANAGER TO RECEIVE MANIFEST

All must be TRUE:

```
✓ Manager is logged in (verified by SessionUtil.getCurrentUser)
✓ TripManifest exists for today
  ├─ Linked to a BusAssignment assigned to this manager
  ├─ SessionType matches the session being viewed
  └─ TripDate = today
✓ NOT in LOCKED time window (manifest finalized)
  └─ Can VIEW during locked or running windows
  └─ Can UPDATE during running window only
✓ BusAssignment.Status = "ACTIVE"
✓ Bus.Status allows operation
```

**If any condition fails:**
- manifest = NULL
- ManagerManifestServlet forwards empty manifest
- Manager sees "No manifest available for this session"

---

## 12. COMPLETE STUDENT JOURNEY EXAMPLE

```
EXAMPLE: Student "Tuan" with parent "Mrs. Smith"

DAY: March 21, 2025 (Friday)

【6:00 PM (18:00) March 20 - 5:00 AM March 21】
 └─ Mrs. Smith opens parent registration
    ├─ Sees Tuan in student list
    ├─ Chooses: SessionType="MORNING", AttendanceChoice="BUS"
    ├─ Adds note: "Doctor appointment at 4 PM, need afternoon trip instead"
    └─ Submits registration

【Database After Registration】
 DailyTripRegistration:
  ├─ RegistrationID: 12345
  ├─ StudentID: 999 (Tuan)
  ├─ TripDate: 2025-03-21
  ├─ SessionType: "MORNING"
  ├─ AttendanceChoice: "BUS" ← Key indicator
  ├─ SourceType: "PARENT"
  ├─ Note: "Doctor appointment at 4 PM..."
  └─ UpdatedAt: 2025-03-20 18:15:00

【5:00 AM - 7:00 AM】
 [SYSTEM SHOULD GENERATE MANIFEST HERE]
  ⚠️ THIS STEP IS MISSING IN THE CODE

【7:00 AM (AFTER generated manifest - if it existed)】
 TripManifest:
  ├─ ManifestID: 555
  ├─ AssignmentID: 10 (Manager Ram, Route 1)
  ├─ TripDate: 2025-03-21
  ├─ SessionType: "MORNING"
  ├─ ManifestStatus: "READY"
  └─ ...

 ManifestStudent:
  ├─ ManifestStudentID: 7890
  ├─ ManifestID: 555
  ├─ StudentID: 999 (Tuan)
  ├─ AttendanceChoice: "BUS"
  ├─ BoardingStatus: "PENDING" ← Will be updated to BOARDED
  └─ ...

【7:15 AM】
 Manager Ram logs in → ManagerManifestServlet
  └─ Sees Tuan in the manifest with status PENDING
     └─ Prepares bus for pickup

【7:45 AM】
 Manager Ram picks up Tuan at specified stop
  └─ ManagerBoardingServlet POST
     └─ Updates ManifestStudent:
        ├─ BoardingStatus: "BOARDED"
        └─ BoardedAt: 2025-03-21 07:45:00

【During Trip (7 AM - 2 PM)】
 Mrs. Smith opens ParentTripStatusServlet
  └─ ParentTripStatusServlet queries ManifestDAO.getStudentTripStatus()
     └─ Shows:
        ├─ Tuan's current boarding status: "BOARDED"
        ├─ Current stop: "Stop 5: Main Street"
        ├─ Time updated: 07:45 AM
        └─ "Trip in progress"

【2:00 PM】
 Trip ends, manifest marked COMPLETED
  └─ ManifestStatus: "COMPLETED"
  └─ FinishedAt: 2025-03-21 13:50:00

【After 6:00 PM】
 Mrs. Smith can register for AFTERNOON session again
  └─ Can now register/modify afternoon trip (for next affected days)
```

---

## CONCLUSION

The Bus Management System has a **well-designed parent registration and time-lockout mechanism**, but **critically lacks the manifest generation logic** that connects parent registrations to operational manifests for managers and drivers. 

**Key Takeaway**: Without implementing manifest generation (likely a stored procedure or batch job that runs during lock time windows), the system cannot function end-to-end. The current code assumes manifests already exist and only handles viewing, updating, and querying them.
