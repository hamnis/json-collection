package net.hamnaberg.json.example;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.parser.CollectionParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ParseFile {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: ");
            System.err.println(ParseFile.class.getName() + " <filename>");
            System.exit(2);
        }
        if ("-".equals(args[0])) {
            parseAndDump(System.in);
        }
        else {
            File file = new File(args[0]);
            if (!file.exists()) {
                System.err.println("File " + file +  " does not exist!");
                System.exit(1);
            }
            FileInputStream stream = new FileInputStream(file);
            try {
                parseAndDump(stream);
            }
            finally {
                stream.close();
            }
        }
    }

    private static void parseAndDump(InputStream stream) throws IOException {
        Collection collection = new CollectionParser().parse(stream);
        System.err.println("Parsed Collection with href: " + collection.getHref());
        System.out.println(collection.toString());
    }
}
