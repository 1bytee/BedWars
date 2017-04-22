import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Test {

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MMMM.YY HH:mm:ss [zzz]", Locale.ENGLISH);
        System.out.println(format.format(new Date()));
    }

}
