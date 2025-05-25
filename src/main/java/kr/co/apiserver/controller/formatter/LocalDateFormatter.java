package kr.co.apiserver.controller.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    public static final DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, formatter);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return formatter.format(object);
    }
}
