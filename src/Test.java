/*
 *  Klasa Test
 *  Zajmuje się tworzeniem testu działania programu
 *
 *  @author Tobiasz Rumian
 *  @version 1.1
 *   Data: 02 Styczeń 2017 r.
 *   Indeks: 226131
 *   Grupa: śr 13:15 TN
 */
class Test {
    public static void main(String[] args) {
        new Server();
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}

        new Client("Ewa");
        new Client("Adam");
    }

}

