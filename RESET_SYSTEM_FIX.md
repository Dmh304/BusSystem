# 🔄 RESET SYSTEM FIX - COMPLETE DAY RESET

## ✅ Feature Implemented
**When clicking the reset button:**
- ✅ Demo time resets to 4:30 AM (next day)
- ✅ All parent registrations for today are deleted
- ✅ All manifests and boarding records are deleted
- ✅ System starts completely fresh as if it's a new day
- ✅ All students must be registered again by parents
- ✅ All boarding statuses reset to PENDING

---

## 📝 Files Modified

### 1. **RegistrationDAO.java** (`dal/RegistrationDAO.java`)
**New Methods Added:**

#### a) `deleteRegistrationsByDate(Date tripDate)`
- Deletes all parent registrations for a specific date
- Used to clear all parent confirmations
- SQL: `DELETE FROM DailyTripRegistration WHERE TripDate = ?`
- Returns: `boolean` (success/failure)

#### b) `deleteRegistrationsByDateAndSession(Date tripDate, String sessionType)`
- Deletes registrations for a specific date AND session type
- Optional method for session-specific reset
- SQL: `DELETE FROM DailyTripRegistration WHERE TripDate = ? AND SessionType = ?`
- Returns: `boolean` (success/failure)

---

### 2. **ManifestDAO.java** (`dal/ManifestDAO.java`)
**New Methods Added:**

#### a) `deleteManifestsByDate(Date tripDate)`
- Deletes all manifests AND all boarding records (ManifestStudent) for a date
- Uses cascading delete (deletes children first, then parents)
- SQL:
  ```sql
  DELETE FROM ManifestStudent WHERE ManifestID IN 
    (SELECT ManifestID FROM TripManifest WHERE TripDate = ?);
  DELETE FROM TripManifest WHERE TripDate = ?;
  ```
- Returns: `boolean` (success/failure)

#### b) `deleteManifestsByDateAndSession(Date tripDate, String sessionType)`
- Deletes manifests for a specific date AND session type
- Optional method for session-specific reset
- SQL:
  ```sql
  DELETE FROM ManifestStudent WHERE ManifestID IN 
    (SELECT ManifestID FROM TripManifest WHERE TripDate = ? AND SessionType = ?);
  DELETE FROM TripManifest WHERE TripDate = ? AND SessionType = ?;
  ```
- Returns: `boolean` (success/failure)

---

### 3. **DemoTimeServlet.java** (`DemoTimeServlet.java`)
**Changes Made:**

#### a) Added Imports:
```java
import dal.ManifestDAO;
import dal.RegistrationDAO;
import java.sql.Date;
```

#### b) Enhanced `doPost()` Method:

**Old Logic:**
```java
if ("reset".equalsIgnoreCase(action)) {
    DemoTimeUtil.resetDemoTime(request.getSession());
    SessionUtil.setSuccess(request.getSession(), "Đã reset thời gian mô phỏng.");
}
```

**New Logic:**
```java
if ("reset".equalsIgnoreCase(action)) {
    // ✅ Reset time to 4:30 AM
    DemoTimeUtil.resetDemoTime(request.getSession());
    
    // ✅ Reset all data for today (new day)
    Date today = new Date(System.currentTimeMillis());
    RegistrationDAO registrationDAO = new RegistrationDAO();
    ManifestDAO manifestDAO = new ManifestDAO();
    
    // Delete all parent registrations for today
    registrationDAO.deleteRegistrationsByDate(today);
    
    // Delete all manifests and boarding records for today
    manifestDAO.deleteManifestsByDate(today);
    
    SessionUtil.setSuccess(request.getSession(), 
        "✅ Đã reset hệ thống: Thời gian mô phỏng → 4:30 AM, Đặt lại đăng ký học sinh, Xóa lịch trình xe.");
}
```

---

## 🔄 Complete Flow After Reset

### **Before Reset**
```
Timeline: 7:00 AM (Morning Running)
├─ Parents registered: Student A (BUS), Student B (WALK)
├─ Manifest created with Student A
├─ Manager marked Student A as BOARDED ✓
├─ Parent sees: Student A on bus
└─ All data in database for today
```

### **Administrator clicks RESET BUTTON**
```
DemoTimeServlet.doPost(action="reset")
    ↓
1. DemoTimeUtil.resetDemoTime()
   └─ Demo time → 4:30 AM ✓
    ↓
2. registrationDAO.deleteRegistrationsByDate(today)
   └─ DELETE FROM DailyTripRegistration WHERE TripDate = today
   └─ Removes: Student A (BUS), Student B (WALK) ✓
    ↓
3. manifestDAO.deleteManifestsByDate(today)
   └─ DELETE FROM ManifestStudent WHERE ManifestID IN (...)
   └─ Removes: Student A boarding record ✓
   └─ DELETE FROM TripManifest WHERE TripDate = today
   └─ Removes: TripManifest record ✓
    ↓
4. User redirected to login page
```

### **After Reset: System is Fresh**
```
Timeline: 4:30 AM (Morning Edit Window Open)
├─ All registrations deleted ✓
├─ All manifests deleted ✓
├─ All boarding records deleted ✓
├─ Parents must register again
├─ Manager will need to wait for lock time to generate new manifest
├─ Status message confirms reset: "✅ Đã reset hệ thống..."
└─ System ready for new day ✓
```

---

## 📊 Data Deletion Cascade

### **When Reset Happens:**

1. **DailyTripRegistration Table**
   ```
   BEFORE: StudentID | TripDate | SessionType | AttendanceChoice
           ----------|----------|-------------|------------------
           1001      | 2026-03-21 | MORNING    | BUS
           1002      | 2026-03-21 | MORNING    | WALK
           1003      | 2026-03-21 | AFTERNOON  | BUS
   
   AFTER:  (EMPTY - all deleted)
   ```

2. **TripManifest Table**
   ```
   BEFORE: ManifestID | AssignmentID | TripDate | SessionType | Status
           -----------|--------------|----------|-------------|--------
           100        | 5            | 2026-03-21 | MORNING    | RUNNING
           101        | 6            | 2026-03-21 | AFTERNOON  | OPEN
   
   AFTER:  (EMPTY - all deleted)
   ```

3. **ManifestStudent Table**
   ```
   BEFORE: ManifestStudentID | ManifestID | StudentID | BoardingStatus
           -------------------|-----------|-----------|----------------
           500                | 100       | 1001      | BOARDED
           501                | 100       | 1005      | PENDING
           502                | 101       | 1003      | PENDING
   
   AFTER:  (EMPTY - all deleted)
   ```

---

## 🔒 Important Notes

1. **Database Referential Integrity:**
   - ManifestStudent has FK to TripManifest (ON DELETE CASCADE not needed, we delete manually)
   - DailyTripRegistration has FK to Student (not deleted, only registrations)
   - Safe deletion order: ManifestStudent first, then TripManifest

2. **Atomic Operation:**
   - Both ManifestStudent and TripManifest deleted in same PreparedStatement
   - If one fails, transaction rolls back (SQL Server handles it)

3. **Date Scope:**
   - Only today's data is deleted
   - Historical data for previous dates remains intact
   - Can use `deleteManifestsByDateAndSession()` for session-specific resets if needed

4. **User Message:**
   - Clear success message confirms what was reset:
     - "✅ Đã reset hệ thống: Thời gian mô phỏng → 4:30 AM, Đặt lại đăng ký học sinh, Xóa lịch trình xe."
   - Translates to: "✅ System reset: Demo time → 4:30 AM, Student registrations reset, Vehicle schedules cleared."

---

## 🧪 Testing Checklist

- [ ] Register students with BUS choice (6 PM evening)
- [ ] Advance time to 7 AM (morning running)
- [ ] Generate manifest (auto-generated after lock time)
- [ ] Mark students as BOARDED
- [ ] Verify parent sees "BOARDED" status
- [ ] Click RESET button
- [ ] Verify demo time is now 4:30 AM
- [ ] Verify all registrations are gone (parents must register again)
- [ ] Verify all manifests are gone (empty TripManifest table)
- [ ] Verify all boarding records are gone (empty ManifestStudent table)
- [ ] Verify parent sees no students in dashboard
- [ ] Register students again and verify system works normally
- [ ] Test afternoon session reset (same flow)

---

## 📈 Summary Table

| Action | Before Reset | After Reset | Status |
|--------|--------------|-------------|--------|
| **Demo Time** | 7:00 AM | 4:30 AM | ✅ Reset |
| **Parent Registrations** | 5 records | 0 records | ✅ Cleared |
| **Manifests** | 2 records | 0 records | ✅ Cleared |
| **Boarding Records** | 10 records | 0 records | ✅ Cleared |
| **Parent Visibility** | See boarded students | See nothing | ✅ Reset |
| **Manager Visibility** | See manifest | Empty | ✅ Reset |
| **System Ready** | For current day | For new day | ✅ Yes |

---

## 🎉 Result

✅ **Complete system reset with single button click**
✅ **Time resets to 4:30 AM**
✅ **All parent registrations cleared**
✅ **All manifests and boarding records cleared**
✅ **System ready for fresh new day**
✅ **Parents must confirm again**
✅ **Manager must wait for lock time to generate new manifest**
