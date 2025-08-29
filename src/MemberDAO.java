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
        String sql = "INSERT INTO members (username, password, email, name, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getName());
            pstmt.setString(5, member.getPhone());

            return pstmt.executeUpdate(); // 실행된 행의 수 리턴 (1이면 성공)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // 실패 시 0 리턴
    }

    // 3. UPDATE SQL 실행 메소드
    public int updateMember(Main.Member member) {
        String sql = "UPDATE members SET password = ?, email = ?, name = ?, phone = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getPassword());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getUsername());

            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 4. DELETE SQL 실행 메소드
    public int deleteMember(String username) {
        String sql = "DELETE FROM members WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}