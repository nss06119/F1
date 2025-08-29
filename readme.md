
### 🏁 1단계: 프로젝트 준비 및 Git 설정 (이전과 동일)

이 부분은 이전 답변과 **완전히 동일**하게 진행해주세요.

1.  **리포지토리 생성 및 팀원 초대** (조장 외 다른 팀원)
2.  **최초 프로젝트 Push** (팀장)
3.  **`.gitignore` 파일 생성** 및 `db.properties` 등록
4.  **팀원들의 `clone` 및 Git 사용자 설정**
5.  각자 **`db.properties` 파일 생성** (DB 접속 정보 기입)

-----

-- member_db.sql
-- IntelliJ IDEA 데이터베이스 연동용

-- 데이터베이스 생성 및 사용
CREATE DATABASE IF NOT EXISTS member_db;
USE member_db;

-- 회원 테이블 생성
CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 테스트용 샘플 데이터
INSERT INTO members (username, password, email, name, phone) VALUES
('admin', 'admin123', 'admin@test.com', '관리자', '010-0000-0000'),
('user1', 'pass123', 'user1@test.com', '홍길동', '010-1111-1111'),
('user2', 'pass456', 'user2@test.com', '김영희', '010-2222-2222');

-- 데이터 확인
SELECT * FROM members;


### 🧬 2단계: 핵심 코드 개발 (파일 2개로 축소)

이제 `src` 폴더에 아래 **2개의 자바 파일**만 생성하고 코드를 붙여넣으세요.

#### 1\. `MemberDAO.java` (DB 연결 + SQL 실행)

이 파일 하나가 DB 연결 관리자와 데이터베이스 명령 실행(DAO) 역할을 모두 수행합니다.

```java
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

// DB 연결과 SQL 실행을 모두 담당하는 클래스
public class MemberDAO {

    // 1. DB 연결을 위한 private 도우미 메소드
    private static Connection getConnection() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("db.properties"));
        // MySQL 드라이버 로딩. DB 종류에 따라 변경 필요.
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
                properties.getProperty("DB_URL"),
                properties.getProperty("DB_USER"),
                properties.getProperty("DB_PASSWORD")
        );
    }

    // 2. INSERT SQL 실행 메소드
    public int insertMember(Main.Member member) {
        String sql = "INSERT INTO members (id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getUsername());
            pstmt.setString(3, member.getEmail());

            return pstmt.executeUpdate(); // 실행된 행의 수 리턴 (1이면 성공)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // 실패 시 0 리턴
    }

    // 3. UPDATE SQL 실행 메소드
    public int updateMember(Main.Member member) {
        String sql = "UPDATE members SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getId());

            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 4. DELETE SQL 실행 메소드
    public int deleteMember(String id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

```

#### 2\. `Main.java` (프로그램 실행 + 데이터 객체)

회원 데이터를 담는 `Member` 클래스를 `Main` 클래스 안에 \*\*내부 클래스(inner class)\*\*로 넣어 파일 수를 줄였습니다.

```java
public class Main {

    // 회원 데이터를 담기 위한 내부 클래스 (파일 분리 안 함)
    public static class Member {
        private String id;
        private String name;
        private String email;

        public Member(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getter 메소드들
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }


    // 프로그램 시작점
    public static void main(String[] args) {
        MemberDAO dao = new MemberDAO();
        System.out.println("### 회원 관리 프로그램 (간소화 버전) ###");

        // 1. 회원 추가 (INSERT 테스트)
        System.out.println("\n[1. 회원 추가]");
        Member newMember = new Member("user123", "박코드", "park@coding.com");
        int insertResult = dao.insertMember(newMember);
        if (insertResult > 0) {
            System.out.println("  -> " + newMember.getUsername() + " 님 정보 추가 성공!");
        } else {
            System.out.println("  -> 추가 실패.");
        }

        // 2. 회원 정보 수정 (UPDATE 테스트)
        System.out.println("\n[2. 회원 정보 수정]");
        Member updateMember = new Member("user123", "박코딩", "park.code@example.com");
        int updateResult = dao.updateMember(updateMember);
        if (updateResult > 0) {
            System.out.println("  -> ID가 " + updateMember.getId() + "인 회원의 정보 수정 성공!");
        } else {
            System.out.println("  -> 수정 실패.");
        }

        // 3. 회원 삭제 (DELETE 테스트)
        System.out.println("\n[3. 회원 삭제]");
        int deleteResult = dao.deleteMember("user123");
        if (deleteResult > 0) {
            System.out.println("  -> ID가 user123인 회원 정보 삭제 성공!");
        } else {
            System.out.println("  -> 삭제 실패.");
        }
    }
}
```

-----

### 🤝 3단계: 협업 및 마무리 (이전과 동일)

코드가 줄었을 뿐, 팀원들과 협업하는 방식은 **완전히 동일**합니다.

1.  **작업 전 `git pull`**: 항상 최신 코드를 받으세요.
2.  **기능 구현**: 팀원과 역할을 나눠 `insert`, `update`, `delete` 메소드 중 하나를 책임지고 완성하거나 테스트 코드를 개선하세요.
3.  **작업 후 `git commit` & `git push`**: 내가 작업한 내용을 팀원들과 공유하세요.

이제 훨씬 적은 파일로 모든 요구사항을 만족시키면서 실습을 진행할 수 있습니다. 바로 시작해보세요\!

