USE master;
GO

IF DB_ID(N'BusManagementSystem') IS NULL
BEGIN
    CREATE DATABASE BusManagementSystem;
END
GO

USE BusManagementSystem;
GO

SET NOCOUNT ON;
GO

/* =========================================================
   DROP ALL FOREIGN KEYS RELATED TO TARGET TABLES
   ========================================================= */
DECLARE @sql NVARCHAR(MAX) = N'';

SELECT @sql = @sql +
    N'ALTER TABLE ' + QUOTENAME(SCHEMA_NAME(pt.schema_id)) + N'.' + QUOTENAME(pt.name) +
    N' DROP CONSTRAINT ' + QUOTENAME(fk.name) + N';' + CHAR(13) + CHAR(10)
FROM sys.foreign_keys fk
JOIN sys.tables pt ON fk.parent_object_id = pt.object_id
JOIN sys.tables rt ON fk.referenced_object_id = rt.object_id
WHERE pt.name IN (
        'ManifestStudent',
        'TripManifest',
        'DailyTripRegistration',
        'Incident',
        'Student',
        'BusAssignment',
        'RouteStop',
        'StopPoint',
        'Route',
        'Bus',
        'UserAccount',
        'Role'
    )
   OR rt.name IN (
        'ManifestStudent',
        'TripManifest',
        'DailyTripRegistration',
        'Incident',
        'Student',
        'BusAssignment',
        'RouteStop',
        'StopPoint',
        'Route',
        'Bus',
        'UserAccount',
        'Role'
    );

IF (@sql <> N'')
BEGIN
    EXEC sp_executesql @sql;
END
GO

DROP TABLE IF EXISTS ManifestStudent;
DROP TABLE IF EXISTS TripManifest;
DROP TABLE IF EXISTS DailyTripRegistration;
DROP TABLE IF EXISTS Incident;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS BusAssignment;
DROP TABLE IF EXISTS RouteStop;
DROP TABLE IF EXISTS StopPoint;
DROP TABLE IF EXISTS Route;
DROP TABLE IF EXISTS Bus;
DROP TABLE IF EXISTS UserAccount;
DROP TABLE IF EXISTS Role;
GO

CREATE TABLE Role (
    RoleID INT IDENTITY(1,1) PRIMARY KEY,
    RoleName VARCHAR(20) NOT NULL UNIQUE
);
GO

CREATE TABLE UserAccount (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) NOT NULL UNIQUE,
    [Password] VARCHAR(100) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    RoleID INT NOT NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE')),
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_UserAccount_Role
        FOREIGN KEY (RoleID) REFERENCES Role(RoleID)
);
GO

CREATE TABLE Bus (
    BusID INT IDENTITY(1,1) PRIMARY KEY,
    PlateNumber VARCHAR(20) NOT NULL UNIQUE,
    BusName NVARCHAR(100) NOT NULL,
    Capacity INT NOT NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE'))
);
GO

CREATE TABLE Route (
    RouteID INT IDENTITY(1,1) PRIMARY KEY,
    RouteName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255) NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE TABLE StopPoint (
    StopID INT IDENTITY(1,1) PRIMARY KEY,
    StopName NVARCHAR(100) NOT NULL,
    AddressDetail NVARCHAR(255) NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE TABLE RouteStop (
    RouteStopID INT IDENTITY(1,1) PRIMARY KEY,
    RouteID INT NOT NULL,
    StopID INT NOT NULL,
    StopOrder INT NOT NULL,
    EstimatedMorningTime TIME NULL,
    EstimatedAfternoonTime TIME NULL,
    CONSTRAINT UQ_RouteStop UNIQUE (RouteID, StopOrder),
    CONSTRAINT FK_RouteStop_Route
        FOREIGN KEY (RouteID) REFERENCES Route(RouteID),
    CONSTRAINT FK_RouteStop_Stop
        FOREIGN KEY (StopID) REFERENCES StopPoint(StopID)
);
GO

CREATE TABLE BusAssignment (
    AssignmentID INT IDENTITY(1,1) PRIMARY KEY,
    BusID INT NOT NULL,
    RouteID INT NOT NULL,
    ManagerUserID INT NULL,
    DriverUserID INT NULL,
    StartDate DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    EndDate DATE NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT FK_BusAssignment_Bus
        FOREIGN KEY (BusID) REFERENCES Bus(BusID),
    CONSTRAINT FK_BusAssignment_Route
        FOREIGN KEY (RouteID) REFERENCES Route(RouteID),
    CONSTRAINT FK_BusAssignment_Manager
        FOREIGN KEY (ManagerUserID) REFERENCES UserAccount(UserID),
    CONSTRAINT FK_BusAssignment_Driver
        FOREIGN KEY (DriverUserID) REFERENCES UserAccount(UserID)
);
GO

-- Clean up any duplicate assignments BEFORE creating UNIQUE indexes
-- Keep the OLDEST assignment for each manager/driver, deactivate newer ones
WITH DuplicateManager AS (
    SELECT 
        AssignmentID,
        ManagerUserID,
        ROW_NUMBER() OVER (PARTITION BY ManagerUserID ORDER BY AssignmentID ASC) as rn
    FROM BusAssignment
    WHERE [Status] = 'ACTIVE' AND ManagerUserID IS NOT NULL
),
DuplicateDriver AS (
    SELECT 
        AssignmentID,
        DriverUserID,
        ROW_NUMBER() OVER (PARTITION BY DriverUserID ORDER BY AssignmentID ASC) as rn
    FROM BusAssignment
    WHERE [Status] = 'ACTIVE' AND DriverUserID IS NOT NULL
)
UPDATE BusAssignment
SET [Status] = 'INACTIVE'
WHERE AssignmentID IN (
    SELECT AssignmentID FROM DuplicateManager WHERE rn > 1
    UNION
    SELECT AssignmentID FROM DuplicateDriver WHERE rn > 1
);
GO

-- Add UNIQUE indexes to prevent duplicate active assignments for same manager/driver
CREATE UNIQUE INDEX UX_BusAssignment_Manager_Active 
    ON BusAssignment(ManagerUserID) 
    WHERE [Status] = 'ACTIVE' AND ManagerUserID IS NOT NULL;

CREATE UNIQUE INDEX UX_BusAssignment_Driver_Active 
    ON BusAssignment(DriverUserID) 
    WHERE [Status] = 'ACTIVE' AND DriverUserID IS NOT NULL;
GO

CREATE TABLE Student (
    StudentID INT IDENTITY(1,1) PRIMARY KEY,
    ParentUserID INT NOT NULL,
    StudentCode VARCHAR(20) NOT NULL UNIQUE,
    FullName NVARCHAR(100) NOT NULL,
    Gender VARCHAR(10) NULL CHECK (Gender IN ('MALE', 'FEMALE')),
    Grade NVARCHAR(20) NULL,
    DefaultRouteID INT NOT NULL,
    DefaultPickupStopID INT NOT NULL,
    DefaultDropoffStopID INT NOT NULL,
    [Status] VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK ([Status] IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT FK_Student_Parent
        FOREIGN KEY (ParentUserID) REFERENCES UserAccount(UserID),
    CONSTRAINT FK_Student_Route
        FOREIGN KEY (DefaultRouteID) REFERENCES Route(RouteID),
    CONSTRAINT FK_Student_PickupStop
        FOREIGN KEY (DefaultPickupStopID) REFERENCES StopPoint(StopID),
    CONSTRAINT FK_Student_DropoffStop
        FOREIGN KEY (DefaultDropoffStopID) REFERENCES StopPoint(StopID)
);
GO

CREATE TABLE DailyTripRegistration (
    RegistrationID INT IDENTITY(1,1) PRIMARY KEY,
    StudentID INT NOT NULL,
    TripDate DATE NOT NULL,
    SessionType VARCHAR(20) NOT NULL
        CHECK (SessionType IN ('MORNING', 'AFTERNOON')),
    AttendanceChoice VARCHAR(20) NOT NULL
        CHECK (AttendanceChoice IN ('BUS', 'SELF', 'OFF')),
    SourceType VARCHAR(20) NOT NULL DEFAULT 'PARENT'
        CHECK (SourceType IN ('PARENT', 'AUTO_DEFAULT')),
    Note NVARCHAR(255) NULL,
    UpdatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT UQ_DailyTripRegistration UNIQUE (StudentID, TripDate, SessionType),
    CONSTRAINT FK_DailyTripRegistration_Student
        FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
);
GO

CREATE TABLE TripManifest (
    ManifestID INT IDENTITY(1,1) PRIMARY KEY,
    AssignmentID INT NOT NULL,
    TripDate DATE NOT NULL,
    SessionType VARCHAR(20) NOT NULL
        CHECK (SessionType IN ('MORNING', 'AFTERNOON')),
    ManifestStatus VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (ManifestStatus IN ('OPEN', 'RUNNING', 'FINISHED')),
    CurrentRouteStopID INT NULL,
    DepartureTime TIME NULL,
    StartedAt DATETIME NULL,
    FinishedAt DATETIME NULL,
    CONSTRAINT UQ_TripManifest UNIQUE (AssignmentID, TripDate, SessionType),
    CONSTRAINT FK_TripManifest_Assignment
        FOREIGN KEY (AssignmentID) REFERENCES BusAssignment(AssignmentID),
    CONSTRAINT FK_TripManifest_CurrentRouteStop
        FOREIGN KEY (CurrentRouteStopID) REFERENCES RouteStop(RouteStopID)
);
GO

CREATE TABLE ManifestStudent (
    ManifestStudentID INT IDENTITY(1,1) PRIMARY KEY,
    ManifestID INT NOT NULL,
    StudentID INT NOT NULL,
    AttendanceChoice VARCHAR(20) NOT NULL
        CHECK (AttendanceChoice IN ('BUS', 'SELF', 'OFF')),
    BoardingStatus VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (BoardingStatus IN ('PENDING', 'BOARDED', 'NO_SHOW', 'NOT_REQUIRED')),
    BoardedAt DATETIME NULL,
    Note NVARCHAR(255) NULL,
    CONSTRAINT UQ_ManifestStudent UNIQUE (ManifestID, StudentID),
    CONSTRAINT FK_ManifestStudent_Manifest
        FOREIGN KEY (ManifestID) REFERENCES TripManifest(ManifestID),
    CONSTRAINT FK_ManifestStudent_Student
        FOREIGN KEY (StudentID) REFERENCES Student(StudentID)
);
GO

CREATE TABLE Incident (
    IncidentID INT IDENTITY(1,1) PRIMARY KEY,
    AssignmentID INT NOT NULL,
    DriverUserID INT NULL,
    Title NVARCHAR(150) NOT NULL,
    Description NVARCHAR(500) NULL,
    IncidentStatus VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (IncidentStatus IN ('OPEN', 'IN_PROGRESS', 'RESOLVED')),
    ReportedAt DATETIME NOT NULL DEFAULT GETDATE(),
    ResolvedAt DATETIME NULL,
    CONSTRAINT FK_Incident_Assignment
        FOREIGN KEY (AssignmentID) REFERENCES BusAssignment(AssignmentID),
    CONSTRAINT FK_Incident_Driver
        FOREIGN KEY (DriverUserID) REFERENCES UserAccount(UserID)
);
GO

INSERT INTO Role (RoleName)
VALUES ('ADMIN'), ('MANAGER'), ('DRIVER'), ('PARENT');
GO

-- ✅ 1 ADMIN + 5 MANAGERS + 5 DRIVERS + 10 PARENTS (Total: 21 accounts)
INSERT INTO UserAccount (Username, [Password], FullName, Email, RoleID)
VALUES
-- ADMIN
('admin1', '123', N'Quản trị viên hệ thống', 'admin1@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'ADMIN')),

-- 5 MANAGERS
('manager1', '123', N'Lê Thị Bích Ngân', 'manager1@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'MANAGER')),
('manager2', '123', N'Trần Văn Hùng', 'manager2@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'MANAGER')),
('manager3', '123', N'Phạm Minh Khoa', 'manager3@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'MANAGER')),
('manager4', '123', N'Nguyễn Thị Hương', 'manager4@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'MANAGER')),
('manager5', '123', N'Hoàng Quốc Khánh', 'manager5@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'MANAGER')),

-- 5 DRIVERS (mỗi driver tương ứng với 1 manager)
('driver1', '123', N'Phùng Văn Hoàng', 'driver1@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'DRIVER')),
('driver2', '123', N'Vũ Minh Tuấn', 'driver2@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'DRIVER')),
('driver3', '123', N'Đặng Thế Anh', 'driver3@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'DRIVER')),
('driver4', '123', N'Lý Quốc Bảo', 'driver4@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'DRIVER')),
('driver5', '123', N'Phan Văn An', 'driver5@school.edu.vn', (SELECT RoleID FROM Role WHERE RoleName = 'DRIVER')),

-- 10 PARENTS (mỗi 2 phụ huynh = 1 manager + 1 driver)
('parent1', '123', N'Lê Hải Đăng', 'parent1@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent2', '123', N'Đồng Mạnh Hùng', 'parent2@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent3', '123', N'Lê Thị Bích Ngân', 'parent3@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent4', '123', N'Lê Thanh Sơn', 'parent4@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent5', '123', N'Phùng Văn Hoàng', 'parent5@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent6', '123', N'Cao Xuân Nam', 'parent6@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent7', '123', N'Trương Thị Liên', 'parent7@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent8', '123', N'Bùi Văn Minh', 'parent8@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent9', '123', N'Võ Thị Hoa', 'parent9@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT')),
('parent10', '123', N'Ngô Hữu Đạt', 'parent10@gmail.com', (SELECT RoleID FROM Role WHERE RoleName = 'PARENT'));
GO

-- ✅ 5 ACTIVE BUSES (for 5 managers) + 1 MAINTENANCE for demo
INSERT INTO Bus (PlateNumber, BusName, Capacity, [Status])
VALUES
('51A-001', N'Hyundai County 2023 - Tuyến 1', 40, 'ACTIVE'),
('51A-002', N'Thaco Town 2023 - Tuyến 2', 35, 'ACTIVE'),
('51A-003', N'Ford Transit 2022 - Tuyến 3', 30, 'ACTIVE'),
('51A-004', N'Isuzu Samco 2023 - Tuyến 4', 35, 'ACTIVE'),
('51A-005', N'Thaco Meadow 2021 - Tuyến 5', 30, 'ACTIVE');
GO

INSERT INTO Route (RouteName, Description, [Status])
VALUES
(N'Tuyến 1 - Trung tâm', N'Tuyến demo chính dùng cho parent/manager/driver', 'ACTIVE'),
(N'Tuyến 2 - Tây Bắc', N'Tuyến phụ', 'ACTIVE'),
(N'Tuyến 3 - Đông Nam', N'Tuyến phụ', 'ACTIVE'),
(N'Tuyến 4 - Bắc Thành phố', N'Tuyến phụ', 'ACTIVE'),
(N'Tuyến 5 - Nam Thành phố', N'Tuyến phụ', 'ACTIVE');
GO

INSERT INTO StopPoint (StopName, AddressDetail, [Status])
VALUES
(N'Trạm 1 - Ngã tư Lê Lợi',         N'Ngã tư Lê Lợi', 'ACTIVE'),
(N'Trạm 2 - Chợ Bến Thành',         N'Chợ Bến Thành', 'ACTIVE'),
(N'Trạm 3 - Công viên 23/9',        N'Công viên 23/9', 'ACTIVE'),
(N'Trạm 4 - Trường THCS Nguyễn Du', N'Trường THCS Nguyễn Du', 'ACTIVE'),
(N'Trạm 5 - Bệnh viện Nhi Đồng',    N'Bệnh viện Nhi Đồng', 'ACTIVE'),
(N'Trạm 6 - Siêu thị Điện Máy',     N'Siêu thị Điện Máy', 'ACTIVE'),
(N'Trạm 7 - Nhà Văn Hóa Quận',      N'Nhà Văn Hóa Quận', 'ACTIVE');
GO

INSERT INTO RouteStop (RouteID, StopID, StopOrder, EstimatedMorningTime, EstimatedAfternoonTime)
VALUES
-- Route 1 stops
(1, 1, 1, '07:00', '16:00'),
(1, 2, 2, '07:10', '16:10'),
(1, 3, 3, '07:20', '16:20'),
(1, 4, 4, '07:30', '16:30'),
(1, 5, 5, '07:40', '16:40'),
(1, 6, 6, '07:50', '16:50'),
(1, 7, 7, '08:00', '17:00'),
-- Route 2 stops
(2, 1, 1, '07:05', '16:05'),
(2, 2, 2, '07:15', '16:15'),
(2, 3, 3, '07:25', '16:25'),
(2, 4, 4, '07:35', '16:35'),
(2, 5, 5, '07:45', '16:45'),
-- Route 3 stops
(3, 2, 1, '07:10', '16:10'),
(3, 3, 2, '07:20', '16:20'),
(3, 4, 3, '07:30', '16:30'),
(3, 5, 4, '07:40', '16:40'),
(3, 6, 5, '07:50', '16:50'),
-- Route 4 stops
(4, 3, 1, '07:15', '16:15'),
(4, 4, 2, '07:25', '16:25'),
(4, 5, 3, '07:35', '16:35'),
(4, 6, 4, '07:45', '16:45'),
(4, 7, 5, '08:00', '17:00'),
-- Route 5 stops
(5, 1, 1, '07:20', '16:20'),
(5, 2, 2, '07:30', '16:30'),
(5, 3, 3, '07:40', '16:40'),
(5, 4, 4, '07:50', '16:50'),
(5, 7, 5, '08:10', '17:10');
GO

-- ✅ 5 BUS ASSIGNMENTS (Manager1+Driver1, Manager2+Driver2, etc.)
-- Each pair of parents' children will travel on one bus with one manager and one driver
INSERT INTO BusAssignment (BusID, RouteID, ManagerUserID, DriverUserID, StartDate, [Status])
VALUES
-- Assignment 1: Manager1 + Driver1 + Bus1 + Route1 (Parents 1-2)
(1, 1, (SELECT UserID FROM UserAccount WHERE Username = 'manager1'), (SELECT UserID FROM UserAccount WHERE Username = 'driver1'), CAST(GETDATE() AS DATE), 'ACTIVE'),
-- Assignment 2: Manager2 + Driver2 + Bus2 + Route2 (Parents 3-4)
(2, 2, (SELECT UserID FROM UserAccount WHERE Username = 'manager2'), (SELECT UserID FROM UserAccount WHERE Username = 'driver2'), CAST(GETDATE() AS DATE), 'ACTIVE'),
-- Assignment 3: Manager3 + Driver3 + Bus3 + Route3 (Parents 5-6)
(3, 3, (SELECT UserID FROM UserAccount WHERE Username = 'manager3'), (SELECT UserID FROM UserAccount WHERE Username = 'driver3'), CAST(GETDATE() AS DATE), 'ACTIVE'),
-- Assignment 4: Manager4 + Driver4 + Bus4 + Route4 (Parents 7-8)
(4, 4, (SELECT UserID FROM UserAccount WHERE Username = 'manager4'), (SELECT UserID FROM UserAccount WHERE Username = 'driver4'), CAST(GETDATE() AS DATE), 'ACTIVE'),
-- Assignment 5: Manager5 + Driver5 + Bus5 + Route5 (Parents 9-10)
(5, 5, (SELECT UserID FROM UserAccount WHERE Username = 'manager5'), (SELECT UserID FROM UserAccount WHERE Username = 'driver5'), CAST(GETDATE() AS DATE), 'ACTIVE');
GO

-- ✅ 32 STUDENTS (2-4 per parent, 10 parents on 5 routes)
INSERT INTO Student (ParentUserID, StudentCode, FullName, Gender, Grade, DefaultRouteID, DefaultPickupStopID, DefaultDropoffStopID, [Status])
VALUES
-- PARENTS 1-2 on Route 1 (Manager1 + Driver1) - 7 students total
((SELECT UserID FROM UserAccount WHERE Username = 'parent1'), 'ST001', N'Nguyễn Minh Anh', 'FEMALE', N'Lớp 6', 1, 1, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent1'), 'ST002', N'Nguyễn Gia Bảo', 'MALE', N'Lớp 3', 1, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent1'), 'ST003', N'Nguyễn Thị Linh', 'FEMALE', N'Lớp 5', 1, 1, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent2'), 'ST004', N'Trần Hoàng Nam', 'MALE', N'Lớp 7', 1, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent2'), 'ST005', N'Trần Bảo Ngọc', 'FEMALE', N'Lớp 4', 1, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent2'), 'ST006', N'Trần Minh Đức', 'MALE', N'Lớp 2', 1, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent2'), 'ST007', N'Trần Thị Hương', 'FEMALE', N'Lớp 6', 1, 4, 4, 'ACTIVE'),

-- PARENTS 3-4 on Route 2 (Manager2 + Driver2) - 5 students total
((SELECT UserID FROM UserAccount WHERE Username = 'parent3'), 'ST008', N'Lê Phương Linh', 'FEMALE', N'Lớp 5', 2, 1, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent3'), 'ST009', N'Lê Văn Khánh', 'MALE', N'Lớp 8', 2, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent4'), 'ST010', N'Phạm Nhật Minh', 'MALE', N'Lớp 8', 2, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent4'), 'ST011', N'Phạm Khánh Vy', 'FEMALE', N'Lớp 2', 2, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent4'), 'ST012', N'Phạm Quốc Huy', 'MALE', N'Lớp 4', 2, 4, 4, 'ACTIVE'),

-- PARENTS 5-6 on Route 3 (Manager3 + Driver3) - 6 students total
((SELECT UserID FROM UserAccount WHERE Username = 'parent5'), 'ST013', N'Hoàng Quốc An', 'MALE', N'Lớp 9', 3, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent5'), 'ST014', N'Hoàng Ngọc Hà', 'FEMALE', N'Lớp 1', 3, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent5'), 'ST015', N'Hoàng Tuấn Kiệt', 'MALE', N'Lớp 5', 3, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent5'), 'ST016', N'Hoàng Thị Thanh', 'FEMALE', N'Lớp 7', 3, 4, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent6'), 'ST017', N'Cao Xuân Nam', 'MALE', N'Lớp 6', 3, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent6'), 'ST018', N'Cao Thị Huyền', 'FEMALE', N'Lớp 3', 3, 4, 4, 'ACTIVE'),

-- PARENTS 7-8 on Route 4 (Manager4 + Driver4) - 7 students total
((SELECT UserID FROM UserAccount WHERE Username = 'parent6'), 'ST019', N'Cao Minh Sơn', 'MALE', N'Lớp 5', 4, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent7'), 'ST020', N'Trương Thị Liên', 'FEMALE', N'Lớp 4', 4, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent7'), 'ST021', N'Trương Văn Hải', 'MALE', N'Lớp 8', 4, 4, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent7'), 'ST022', N'Trương Thị Mỹ', 'FEMALE', N'Lớp 2', 4, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent7'), 'ST023', N'Trương Quốc Vinh', 'MALE', N'Lớp 6', 4, 5, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent8'), 'ST024', N'Bùi Văn Minh', 'MALE', N'Lớp 7', 4, 5, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent8'), 'ST025', N'Bùi Thị An', 'FEMALE', N'Lớp 3', 4, 4, 4, 'ACTIVE'),

-- PARENTS 9-10 on Route 5 (Manager5 + Driver5) - 7 students total
((SELECT UserID FROM UserAccount WHERE Username = 'parent9'), 'ST026', N'Võ Thị Hoa', 'FEMALE', N'Lớp 5', 5, 1, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent9'), 'ST027', N'Võ Văn Tuấn', 'MALE', N'Lớp 9', 5, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent9'), 'ST028', N'Võ Thị Tâm', 'FEMALE', N'Lớp 4', 5, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent10'), 'ST029', N'Ngô Hữu Đạt', 'MALE', N'Lớp 6', 5, 2, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent10'), 'ST030', N'Ngô Thị Linh', 'FEMALE', N'Lớp 2', 5, 3, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent10'), 'ST031', N'Ngô Văn Hiệu', 'MALE', N'Lớp 7', 5, 1, 4, 'ACTIVE'),
((SELECT UserID FROM UserAccount WHERE Username = 'parent10'), 'ST032', N'Ngô Thị Hằng', 'FEMALE', N'Lớp 3', 5, 4, 4, 'ACTIVE');
GO

-- ✅ Optional: Sample DailyTripRegistration (parents should register themselves)
-- DELETE old data to start fresh
DELETE FROM ManifestStudent;
DELETE FROM TripManifest;
DELETE FROM DailyTripRegistration;
GO

-- DECLARE @Today DATE = CAST(GETDATE() AS DATE);
-- INSERT INTO DailyTripRegistration - Left empty for parents to register
GO
INSERT INTO Incident (AssignmentID, DriverUserID, Title, Description, IncidentStatus, ReportedAt, ResolvedAt)
VALUES
(
    (SELECT TOP 1 AssignmentID FROM BusAssignment WHERE RouteID = 1 AND [Status] = 'ACTIVE'),
    (SELECT UserID FROM UserAccount WHERE Username = 'driver1'),
    N'Kiểm tra định kỳ',
    N'Không có sự cố thực tế, bản ghi mẫu để test chức năng',
    'RESOLVED',
    DATEADD(DAY, -1, GETDATE()),
    GETDATE()
);
GO
