import java.io.File;
import java.util.Scanner;

/*
 * Etapa 2 - Interagindo com o syslog
 *
 * Le o syslog do Linux linha por linha e mostra na tela somente
 * os eventos de conexao e desconexao de rede, com data e hora.
 */
public class LeitorSyslog {

    public static void main(String[] args) throws Exception {

        // Caminho do syslog (pode ser passado na linha de comando)
        String caminho = "/var/log/syslog";
        if (args.length > 0) {
            caminho = args[0];
        }

        Scanner arquivo = new Scanner(new File(caminho));

        int conexoes = 0;
        int desconexoes = 0;

        System.out.println("EVENTOS DE REDE NO SYSLOG (" + caminho + ")");
        System.out.println();

        // Enquanto existir linha no arquivo...
        while (arquivo.hasNextLine()) {
            String linha = arquivo.nextLine();

            // Os 15 primeiros caracteres da linha do syslog sao a data e a hora
            String dataHora = linha.substring(0, 15);

            // Testamos a desconexao primeiro, porque a palavra "disconnected"
            // tem dentro dela a palavra "connected" e isso confundiria o programa.
            if (linha.contains("Link DOWN") || linha.contains("carrier lost")
                    || linha.contains("DISCONNECTED") || linha.contains("DHCPRELEASE")) {

                System.out.println(dataHora + "  DESCONEXAO  ->  " + linha);
                desconexoes = desconexoes + 1;

            } else if (linha.contains("Link UP") || linha.contains("carrier acquired")
                    || linha.contains("CONNECTED") || linha.contains("bound to")) {

                System.out.println(dataHora + "  CONEXAO     ->  " + linha);
                conexoes = conexoes + 1;
            }
        }

        arquivo.close();

        System.out.println();
        System.out.println("Conexoes: " + conexoes + "   Desconexoes: " + desconexoes);
    }
}
