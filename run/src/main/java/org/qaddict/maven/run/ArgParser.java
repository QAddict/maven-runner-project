package org.qaddict.maven.run;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Stream;

/**
 * As maven cannot simply accept some remainder as free form command line arguments to be passed to it's goal,
 * the arguments must be passed within a system property (-Dargs=parameters). So the plugins will receive such
 * parameters within one system property, and they need to be properly escaped and parsed, taking the escaping
 * into account.
 * The parser recognizes space delimited parameters. To allow special characters, it allows double/single quoting
 * and escaping a single character by backslash.
 * The parser uses very simple recursive descent algorithm.
 */
public class ArgParser {

    private ArgParser() {}

    public static Stream<String> parse(String commandLine) {
        if(commandLine == null)
            return Stream.empty();
        List<StringBuilder> result = new ArrayList<>();
        StringReader reader = new StringReader(commandLine);
        try {
            while(parseArg(reader, result)) {}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.stream().map(Object::toString);
    }


    private static boolean parseArg(StringReader input, List<StringBuilder> result) throws IOException {
        for(int c = input.read(); c > 0; c = input.read()) {
            if(!Character.isSpaceChar(c)) switch(c) {
                case '"': return parseValue(input.read(), input, result, i -> i == '"');
                case '\'': return parseValue(input.read(), input, result, i -> i == '\'');
                default: return parseValue(c, input, result, Character::isSpaceChar);
            }
        }
        return false;
    }

    private static boolean parseValue(int c, StringReader reader, List<StringBuilder> result, IntPredicate end) throws IOException {
        StringBuilder builder = new StringBuilder();
        result.add(builder);
        while(!end.test(c)) {
            if(c <= 0) return false;
            if(c == '\\') c = reader.read();
            builder.append((char) c);
            c = reader.read();
        }
        return true;
    }
}
