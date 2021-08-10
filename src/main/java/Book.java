import java.io.*;

public class Book implements Serializable {


        private static final long serialVersionUID = -2936687026040726549L;
        private String bookName;
        private transient String description;
        private transient int copies;

        // getters and setters

    public static void serialize(Book book) throws Exception {
        FileOutputStream file = new FileOutputStream("newbook.txt");
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(book);
        out.close();
        file.close();
    }

    public static Book deserialize() throws Exception {
        FileInputStream file = new FileInputStream("newbook.txt");
        ObjectInputStream in = new ObjectInputStream(file);
        Book book = (Book) in.readObject();
        in.close();
        file.close();
        return book;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
