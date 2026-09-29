import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
        while (true) {
            String value = ask("Введите дату рождения (гггг-мм-дд): ");
            if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Ошибка! Неверный формат даты. Пример: 2005-04-17");
                continue;
            }
            String[] p = value.split("-");
            int year = Integer.parseInt(p[0]);
            int month = Integer.parseInt(p[1]);
            int day = Integer.parseInt(p[2]);
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
            if (value.matches("\\d+\\.\\d{2}")) {
                return Double.parseDouble(value);
            }
            System.out.println("Ошибка! Введите температуру в формате XX.XX.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        PatientRegistry registry = new PatientRegistry();
        PatientReader reader = new PatientReader();

        while (true) {
            Patient patient = reader.createPatient();
            registry.add(patient);

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
