import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatientRegistry {
    private final List<Patient> patients = new ArrayList<>();

    void add(Patient patient) {
        patients.add(patient);
    }

    List<Patient> getAll() {
        return patients;
    }
}

class PatientReader {
    private final Scanner scanner = new Scanner(System.in);

    String ask(String question) {
        System.out.print(question);
        return scanner.nextLine();
    }

    Patient createPatient() {
        System.out.println();
        System.out.println("Введите данные пациента");
        System.out.println();

        String name = read("Введите ФИО: ", value -> !value.trim().isEmpty());
        String passport = read("Введите паспорт (12 34-567890): ", value -> value.matches("\\d{2} \\d{2}-\\d{6}"));
        BirthDate birthDate = readBirthDate();
        String phone = read(
                "Введите номер телефона (+X(XXX) XXX-XX-XX или X(XXX) XXX-XXXX): ",
                value -> value.matches("\\+\\d\\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}")
                        || value.matches("\\d\\(\\d{3}\\) \\d{3}-\\d{4}"));
        double temperature = readTemperature();

        return new Patient(name, passport, birthDate, phone, temperature);
    }

    private String read(String question, java.util.function.Predicate<String> validator) {
        while (true) {
            String value = ask(question);
            if (validator.test(value)) {
                return value;
            }
            System.out.println("Ошибка! Неверные данные.");
        }
    }

    private BirthDate readBirthDate() {
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        while (true) {
            String value = ask("Введите дату рождения (гггг-мм-дд): ");
            Matcher matcher = pattern.matcher(value.trim());
            if (!matcher.matches()) {
                System.out.println("Ошибка! Неверный формат даты. Пример: 2005-04-17");
                continue;
            }
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            try {
                LocalDate.of(year, month, day);
                return new BirthDate(day, month, year);
            } catch (DateTimeException e) {
                System.out.println("Ошибка! Такой даты не существует.");
            }
        }
    }

    private double readTemperature() {
        while (true) {
            String value = ask("Введите температуру (например, 36.60): ");
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Не удалось распознать температуру: " + value);
            }
        }
    }
}

class PatientFileReader {
    private final Path path;

    PatientFileReader() {
        Path local = Paths.get("patients.txt");
        this.path = Files.isRegularFile(local) ? local : Paths.get("patients-java", "patients.txt");
    }

    List<Patient> load() {
        List<Patient> patients = new ArrayList<>();
        if (!Files.isRegularFile(path)) {
            System.out.println("Файл с пациентами не найден: " + path);
            return patients;
        }
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\|");
                if (p.length != 5) {
                    System.out.println("Пропущена некорректная строка: " + line);
                    continue;
                }
                try {
                    patients.add(new Patient(
                            p[0].trim(),
                            p[1].trim(),
                            BirthDate.parse(p[2]),
                            p[3].trim(),
                            Double.parseDouble(p[4].trim())));
                } catch (IllegalArgumentException e) {
                    System.out.println("Пропущена некорректная строка: " + line);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return patients;
    }

    void append(Patient patient) {
        BirthDate d = patient.getBirthDate();
        String line = String.format(Locale.US, "%s|%s|%04d-%02d-%02d|%s|%.2f",
                patient.getName(), patient.getPassport(),
                d.getYear(), d.getMonth(), d.getDay(),
                patient.getPhone(), patient.getTemperature());
        try {
            Files.write(path, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        PatientRegistry registry = new PatientRegistry();
        PatientReader reader = new PatientReader();
        PatientFileReader fileReader = new PatientFileReader();

        for (Patient patient : fileReader.load()) {
            registry.add(patient);
        }
        System.out.println("Загружено пациентов из файла: " + registry.getAll().size());

        while (true) {
            Patient patient = reader.createPatient();
            registry.add(patient);
            fileReader.append(patient);

            String answer = reader.ask("\nХотите создать ещё одного пациента? (да/нет): ");
            if (!answer.equalsIgnoreCase("да")) {
                break;
            }
        }

        System.out.println();
        System.out.println("===== Все пациенты =====");
        for (Patient patient : registry.getAll()) {
            printPatient(patient);
        }
    }

    static void printPatient(Patient patient) {
        BirthDate d = patient.getBirthDate();
        System.out.println();
        System.out.println("----- Данные пациента -----");
        System.out.println("ФИО: " + patient.getName());
        System.out.println("Паспорт: " + patient.getPassport());
        System.out.println("Дата рождения: " + String.format("%04d-%02d-%02d", d.getYear(), d.getMonth(), d.getDay()));
        System.out.println("Телефон: " + patient.getPhone());
        System.out.println("Температура: " + String.format(Locale.US, "%.2f", patient.getTemperature()));
    }
}
