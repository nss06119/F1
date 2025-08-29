public class Main {

    // 회원 데이터를 담기 위한 내부 클래스 (파일 분리 안 함)
    public static class Member {
        private String username;
        private String password;
        private String email;
        private String name;
        private String phone;

        public Member(String username, String password, String email, String name, String phone) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.name = name;
            this.phone = phone;
        }

        // Getter 메소드들
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
    }


    // 프로그램 시작점
    public static void main(String[] args) {
        MemberDAO dao = new MemberDAO();
        System.out.println("### 회원 관리 프로그램 (간소화 버전) ###");

        // 1. 회원 추가 (INSERT 테스트)
        System.out.println("\n[1. 회원 추가]");
        // DB 테이블 구조에 맞게 Member 객체 생성
        Member newMember = new Member("new_user", "newpass123", "newuser@test.com", "새로운 유저", "010-9999-9999");
        int insertResult = dao.insertMember(newMember);
        if (insertResult > 0) {
            System.out.println("  -> " + newMember.getUsername() + " 님 정보 추가 성공!");
        } else {
            System.out.println("  -> 추가 실패.");
        }

        // 2. 회원 정보 수정 (UPDATE 테스트)
        System.out.println("\n[2. 회원 정보 수정]");
        // UPDATE는 username을 기준으로 비밀번호와 전화번호를 수정합니다.
        Member updateMember = new Member("new_user", "updatedpass", "updated@test.com", "수정된 유저", "010-8888-8888");
        int updateResult = dao.updateMember(updateMember);
        if (updateResult > 0) {
            System.out.println("  -> ID가 " + updateMember.getUsername() + "인 회원의 정보 수정 성공!");
        } else {
            System.out.println("  -> 수정 실패.");
        }

        // 3. 회원 삭제 (DELETE 테스트)
        System.out.println("\n[3. 회원 삭제]");
        // DELETE는 username을 기준으로 삭제합니다.
        int deleteResult = dao.deleteMember("new_user");
        if (deleteResult > 0) {
            System.out.println("  -> ID가 new_user인 회원 정보 삭제 성공!");
        } else {
            System.out.println("  -> 삭제 실패.");
        }
    }
}