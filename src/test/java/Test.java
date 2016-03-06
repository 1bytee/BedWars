import org.apache.commons.codec.binary.Base64;

public class Test {

    public static void main(String[] args) {

        String s = "eyJ0aW1lc3RhbXAiOjE0NTcyMTg4NjAxMjMsInByb2ZpbGVJZCI6IjA2OWE3OWY0NDRlOTQ3MjZhNWJlZmNhOTBlMzhhYWY1IiwicHJvZmlsZU5hbWUiOiJOb3RjaCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hMTE2ZTY5YTg0NWUyMjdmN2NhMWZkZGU4YzM1N2M4YzgyMWViZDRiYTYxOTM4MmVhNGExZjg3ZDRhZTk0In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lZWMzY2FiZmFlZWQ1ZGFmZTYxYzY1NDYyOTdlODUzYTU0N2MzOWVjMjM4ZDdjNDRiZjRlYjRhNDlkYzFmMmMwIn19fQ==";

        byte[] bytes = Base64.decodeBase64(s);
        System.out.println(new String(bytes));
    }

}
