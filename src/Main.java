import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner sa = new Scanner(System.in);
        Scanner sd = new Scanner(System.in);
        Database db = new Database();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatdate = now.format(formatter);
        Timestamp finaltime = Timestamp.valueOf(formatdate);

        if (!db.open()) {
            System.out.println("Can't open");
            return;
        }

        System.out.println("주문을 하시려면 1번 아니면 조회만 하고 싶다면 2번을 눌러주세요.");
        Integer ss = sc.nextInt();
        switch (ss) {
            case 1:
                System.out.println();
                System.out.println("                    메뉴     ||  가격    ");
                List<menu> menus = db.menu();
                for (menu menu : menus) {
                System.out.printf("%d  %26s  ||  %s%n", menu.getId(), menu.getSecond_choice(), menu.getPrice());
                }
                System.out.println();

                System.out.println("메뉴를 번호로 선택해주세요: ");
                Integer m1 = sc.nextInt();
                List<option_choice> option_choice = db.option_choice(m1);

                System.out.println();
                System.out.println("        추가  ||  가격");
                for (option_choice option_choice1 : option_choice) {
                System.out.printf("%d   %11s  ||  %d%n", option_choice1.getId(), option_choice1.getAdd_choice(), option_choice1.getAdd_price());
                }
                System.out.println();

                System.out.println("추가 옵션번호를 입력해주세요:");
                Integer m2 = sc.nextInt();
                List<option_choice> option_choices = db.option_choice1(m1, m2);
                List<menu> second_menu = db.menu1(m1);
                for (menu scmenu : second_menu) {
                    for (option_choice option_choice2 : option_choices) {
                    System.out.printf("%s + %s = %d%n", scmenu.getSecond_choice(), option_choice2.getAdd_choice(), option_choice2.getAdd_price());
                    }
                }

                System.out.println("선택한 메뉴가 맞습니까?\n y or n");
                String s = sa.nextLine();
                if (s.equals("y")) {
                    System.out.println("적립을 하시겠습니까?\n y or n");
                    String n = sa.nextLine();
                    if (n.equals("y")) {
                        System.out.println("회원 번호를 입력해주세요");
                        Integer p = sa.nextInt();
                        List<amounts> amount = db.amounts(p, m1, m2);
                        List<members> members = db.members(p);
                        if (members.isEmpty() || null == members) {
                            for (option_choice option_choice2 : option_choices) {
                            for (menu scmenu : second_menu) {
                                System.out.printf("신규가입을 축하합니다.%n최종 메뉴와 가격입니다.%n%s + %s = %d%n감사합니다.", scmenu.getSecond_choice(), option_choice2.getAdd_choice(), option_choice2.getAdd_price());
                            }
                            }
                            List<members> insert = db.insert(p);
                        }
                        else {
                            List<members> discount = db.discount(p);
                            for (members members2 : discount) {
                                System.out.println(p + "의 등급 : " + members2.getGrade() + ", 최종 금액: " + members2.getTotal_amount() + "\n감사합니다.");
                            }
                            List<members> upamount = db.update_total_amount(p);
                            List<members> upcount = db.update_count(p);
                            List<members> upgrade = db.update_grade(p);
                        }
                    } else if (n.equals("n")) {
                        for (menu scmenu : second_menu) {
                            for (option_choice option_choice2 : option_choices) {
                                System.out.printf("최종 메뉴와 가격입니다.%n%s + %s = %d%n감사합니다.%n", scmenu.getSecond_choice(), option_choice2.getAdd_choice(), option_choice2.getAdd_price());
                            }
                        }
                        db.close();
                    }
                }
                else if (s.equals("n")) {
                    System.out.println("재주문해주세요!");
                    db.close();
                }
                break;

            case 2:
                System.out.println("조회하고 싶은 핸드폰 번호를 입력하세요.");
                Integer p = sa.nextInt();
                List<members> upmember = db.update_members(p);
                for (members membersfinal : upmember) {
                    System.out.println(membersfinal.getPhone_number() + "의 총 구매금액 : " + membersfinal.getTotal_amount() + ", 총 구매 횟수 : " + membersfinal.getCounts() + ", 최종 갱신 등급 : " + membersfinal.getGrade());
                }

                System.out.println("조회하고 싶은 년도와 달은 입력하세요. ex)2022-01");
                String ti = sd.nextLine();
                List<amounts> select_time = db.select_amounts(ti, p);
                for (amounts time : select_time) {
                    System.out.println(time.getPhone() + ", " + time.getSecond_choice() + ", " + time.getAdd_choice() + ", " + time.getAmount() + ", " + time.getTimes());
                }
                break;
        }
    }
}