import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el número de usuarios: ");
        int numUsuarios = scanner.nextInt();

        AtomicInteger contrasenasValidas = new AtomicInteger(0);


        String filePath = "registro.txt";


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < numUsuarios; i++) {
                Thread thread = new PasswordValidatorThread(contrasenasValidas, writer);
                thread.start();
            }

            while (Thread.activeCount() > 1) {
                Thread.yield();
            }


            System.out.println("Total de contraseñas válidas: " + contrasenasValidas.get());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de registro: " + e.getMessage());
        }
    }
}

class PasswordValidator {
    public static boolean validate(String password) {
        String regex = "^(?=.*[a-z].*[a-z].*[a-z])(?=.*[A-Z].*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$";
        return password.matches(regex);
    }
}

class PasswordValidatorThread extends Thread {
    private AtomicInteger contrasenasValidas;
    private BufferedWriter writer;

    public PasswordValidatorThread(AtomicInteger contrasenasValidas, BufferedWriter writer) {
        this.contrasenasValidas = contrasenasValidas;
        this.writer = writer;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine();

        try {

            boolean isValid = PasswordValidator.validate(password);
            String resultado = isValid ? "Válida" : "No válida";
            writer.write("Contraseña: " + password + " - " + resultado);
            writer.newLine();


            if (isValid) {
                contrasenasValidas.incrementAndGet();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de registro: " + e.getMessage());
        }
    }
}
