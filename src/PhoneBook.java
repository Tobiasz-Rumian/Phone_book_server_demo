/*
 *  Klasa PhoneBook
 *  Zajmuje się przechowywaniem bazy numerów.
 *
 *  @author Tobiasz Rumian
 *  @version 1.1
 *   Data: 02 Styczeń 2017 r.
 *   Indeks: 226131
 *   Grupa: śr 13:15 TN
 */

import java.io.*;
import java.util.concurrent.ConcurrentSkipListMap;

class PhoneBook implements Serializable {
    private static final long serialVersionUID = 1L;
    private ConcurrentSkipListMap<String, String> book = new ConcurrentSkipListMap<>();

    PhoneBook() {
    }

    synchronized String get(String nick) {
        if (!book.containsKey(nick)) return "Podany nick nie istnieje";
        return book.get(nick);
    }

    synchronized String put(String nick, String number) {
        if (number.length() < 9 || number.length() > 9) return "Numer powinien posiadać 9 cyfr!";
        if (book.containsKey(nick)) return "Podany nick jest już zajęty";
        book.put(nick, number);
        return ok();
    }

    synchronized String replace(String nick, String number) {
        if (number.length() < 9 || number.length() > 9) return "Numer powinien posiadać 9 cyfr!";
        if (!book.containsKey(nick)) return "Podany nick nie istnieje";
        book.replace(nick, number);
        return ok();
    }

    synchronized String delete(String nick) {
        if (!book.containsKey(nick)) return "Podany nick nie istnieje";
        book.remove(nick);
        return ok();
    }

    synchronized String list() {
        StringBuilder listBuilder = new StringBuilder("OK\n");
        for (ConcurrentSkipListMap.Entry<String, String> entry : book.entrySet()) {
            listBuilder.append("Nick: ").append(entry.getKey()).append("\n");
        }
        return listBuilder.toString();
    }

    synchronized String save(String fileName) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(book);
        out.close();
        return ok();
    }


    synchronized String load(String fileName) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        book = (ConcurrentSkipListMap<String, String>) in.readObject();
        in.close();
        return ok();
    }

    private String ok() {
        return "OK";
    }

}
