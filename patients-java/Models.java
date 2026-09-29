import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BirthDate {
    private static final Pattern FORMAT = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    static BirthDate parse(String value) {
        Matcher matcher = FORMAT.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат даты. Пример: 2005-04-17");
        }
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));
        try {
            LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Такой даты не существует.");
        }
        return new BirthDate(day, month, year);
    }

    private int day;
    private int month;
    private int year;

    BirthDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

class Patient {
    private String name;
    private String passport;
    private BirthDate birthDate;
    private String phone;
    private double temperature;

    Patient(String name, String passport, BirthDate birthDate, String phone, double temperature) {
        this.name = name;
        this.passport = passport;
        this.birthDate = birthDate;
        this.phone = phone;
        this.temperature = temperature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
