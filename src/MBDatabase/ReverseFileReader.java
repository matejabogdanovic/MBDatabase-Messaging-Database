package MBDatabase;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReverseFileReader {
    private RandomAccessFile raf;
    private long cursorPosition;

    public ReverseFileReader(String filePath) throws IOException {
        File file = new File(filePath);
        this.raf = new RandomAccessFile(file, "r");
        this.cursorPosition = file.length() - 1; // Početak od kraja fajla
    }

    public String readPreviousLine() throws IOException {
        if (cursorPosition < 0) {
            return null; // Nema više linija za čitanje
        }

        StringBuilder line = new StringBuilder();
        boolean hasRead = false;

        while (cursorPosition >= 0) {
            raf.seek(cursorPosition);
            char c = (char) raf.read();
            cursorPosition--;

            if (c == '\n' && hasRead) {
                break; // Prekidamo kada naiđemo na novi red
            }

            if (c != '\n' && c != '\r') {
                line.append(c);
                hasRead = true;
            }
        }

        return line.length() > 0 ? line.reverse().toString() : null;
    }

    public void close() throws IOException {
        raf.close();
    }


}
