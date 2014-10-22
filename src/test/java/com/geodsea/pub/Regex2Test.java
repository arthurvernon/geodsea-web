package com.geodsea.pub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arthur Vernon on 29/08/2014.
 */
public class Regex2Test {


    public static void main(String[] args) {

        String[] split = "123 456".split("[^\\d]");

        for (String s : split)
        {
            System.out.println(s);
        }
    }
}
