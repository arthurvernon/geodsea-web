package com.geodsea.pub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arthur Vernon on 29/08/2014.
 */
public class RegexTest {

    private static final Pattern NUMERIC_CHAR = Pattern.compile(".*\\p{N}.*");
    private static final Pattern LOWERCASE_CHAR = Pattern.compile(".*\\p{Ll}.*");
    private static final Pattern UPPERCASE_CHAR = Pattern.compile(".*\\p{Lu}.*");
    private static Pattern SYMBOL_CHAR = Pattern.compile(".*[\\p{P}\\p{S}].*");


    public static void main(String[] args) {

        String str = "[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>%@#&_:\";',/]";
        Pattern pattern = Pattern.compile(str);
        String chars = "abc789ABC~`!@#$%^&*()_+-={}|[]\\:\";'<>?,./'";

//        for (char c : chars.toCharArray())
//            System.out.println("" + c + " " + pattern.matcher(""+c).matches());
//


        pattern = Pattern.compile(".*\\p{P}.*");
        pattern = Pattern.compile(".*[\\p{P}\\p{S}].*");


        for (char c : chars.toCharArray())
            System.out.println("" + c + " " + pattern.matcher(""+c).matches());

        System.out.println("x)y " + pattern.matcher("x)y").matches());


        check("a");
        check("aa");
        check("aaaa");
        check("aaaaa");
        check("aaaaaa");
        check("aaaaaaa");
        check("aaaaaaaa");
        check("aaaaaaaaa");
        check("aaaaaaaaaa");
        check("aaaaaaaaaaa");
        check("a3$");
        check("a3$      ");
        check("@4NoteB00k");
        check("aAaAaA");

    }

    private static void check(String s) {
        System.out.println(s + ": " + strength(s));
    }

    public static final int strength(String pword)
    {
        int tests = 0;

        Matcher m = SYMBOL_CHAR.matcher(pword);

        if (m.matches())
            tests ++ ;

        m.usePattern(LOWERCASE_CHAR);
        if (m.matches())
            tests ++ ;

        m.usePattern(UPPERCASE_CHAR);
        if (m.matches())
            tests ++ ;

        m.usePattern(NUMERIC_CHAR);
        if (m.matches())
            tests ++ ;


        int value = tests * 10;
        value += 2 * pword.length() + ((pword.length() >= 10) ? 1 : 0);

        // penality (short password)
        value = (pword.length() <= 6) ? Math.min(value, 10) : value;

        // penality (poor variety of characters)
        value = (tests == 1) ? Math.min(value, 10) : value;
        value = (tests == 2) ? Math.min(value, 20) : value;
        value = (tests == 3) ? Math.min(value, 40) : value;

        return value;

    }


}
