import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Observation> trainObservations;
        List<Observation> testObservations;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj nazwe pliku ze zbiorem treningowym: ");
        String trainObservationsFile = br.readLine();
        System.out.println("Podaj nazwe pliku ze zbiorem testowym, w przypadku własnych danych wpisz 'q': ");
        String testObservationsFile = br.readLine();
        trainObservations = listObsv(trainObservationsFile);
        System.out.println("Podaj K:");
        int k = Integer.parseInt(br.readLine());
        if (testObservationsFile.equals("q")) {
            while (true) {
                System.out.println("Wprowadz prawidlowa obserwacje rozdzieloną przecinkiem ',', aby wyjsc wpisz 'exit'): ");
                String ln = br.readLine();
                if (ln.equals("exit")) {
                    break;
                }
                String[] temp = ln.split(",");
                List<Double> attributesColumn = new ArrayList<>();
                for (int i = 0; i < temp.length - 1; i++) {
                    attributesColumn.add(Double.parseDouble(temp[i]));
                }

                List<Observation> ownObservation = new ArrayList<>();
                ownObservation.add(new Observation(attributesColumn, temp[temp.length - 1]));
                algorithm(trainObservations, ownObservation, k);
            }
        } else {
            testObservations = listObsv(testObservationsFile);

            algorithm(trainObservations, testObservations, k);
        }
    }
    // wczytuje dane z pliku i zwraca listę obserwacji (klasa 'Observation' zawiera atrybuty)
    public static List<Observation> listObsv(String fileName) throws IOException {
        String ln;
        List<Observation> obsvSet = new ArrayList<>();
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        while ((ln = br.readLine()) != null && (!ln.equals(""))) {
            String[] tmp = ln.split(",");
            List<Double> attributesColumn = new ArrayList<>();
            for (int i = 0; i < tmp.length - 1; i++) {
                attributesColumn.add(Double.parseDouble(tmp[i]));
            }
            obsvSet.add(new Observation(attributesColumn, tmp[tmp.length - 1]));
        }
        return obsvSet;
    }

    //oblicza odległość między dwoma obserwacjami
    public static double obsvDistance(Observation obsv1, Observation obsv2) {
        if (obsv1 == null || obsv2 == null) {
            System.err.println("NULL!");
            return 1;
        }
        double distance = 0;
        for (int i = 0; i < obsv1.attributes.size(); i++) {
            distance += Math.pow(obsv1.attributes.get(i) - obsv2.attributes.get(i), 2);
        }
        return distance;
    }

    /*implementuje algortym k-NN. Przyjmuje jako argumenty listy obserwacji treningowych i testowych oraz wartość k.
        Dla każdej obserwacji testowej znajduje k najbliższych obserwacji treningowych, korzystając z
        funkcji obsvDistance(). Następnie oblicza, ile z tych obserwacji treningowych należy do każdej z etykiet
        i wybiera etykietę, która występuje najczęściej. Porównuje wybraną etykietę z rzeczywistą etykietą i
        zwraca dokładność klasyfikacji dla wszystkich obserwacji testowych.
    */
    public static void algorithm(List<Observation> trainObsv, List<Observation> testObsv, int k) {
        System.out.println("K=" + k);
        String answer = "";
        double correctAnswer = 0;
        for (Observation currentObsv : testObsv) {
            List<Distance> distanceList = new ArrayList<>();
            for (Observation trainedObsv : trainObsv) {
                distanceList.add(new Distance(trainedObsv, currentObsv, obsvDistance(currentObsv, trainedObsv)));
            }
            Collections.sort(distanceList);
            double matching = 0;
            List<String> resultList = new ArrayList<>();
            Set<String> resultSet = new HashSet<>();
            boolean needsSmallerK = false;

            for (int j = 0; j < k; j++) {
                resultList.add(distanceList.get(j).trainObsv.type);
                resultSet.add(distanceList.get(j).trainObsv.type);
                if (distanceList.get(j).trainObsv.type.equals(currentObsv.type))
                    matching++;
            }
            int localK = k;
            while (((matching / k) * 100) == 50) {
                needsSmallerK = true;
                localK -= 1;
                matching = 0;
                resultList.clear();
                resultSet.clear();
                for (int j = 0; j < localK; j++) {
                    resultList.add(distanceList.get(j).trainObsv.type);
                    resultSet.add(distanceList.get(j).trainObsv.type);
                    if (distanceList.get(j).trainObsv.type.equals(currentObsv.type))
                        matching++;
                }
            }
            int max = 0;
            for (String string : resultSet) {
                if (Collections.frequency(resultList, string) > max) {
                    max = Collections.frequency(resultList, string);
                    answer = string;
                }
            }
            if (answer.equals(currentObsv.type))
                correctAnswer++;
            System.out.println();
            System.out.println("Odpowiedz algorytmu: " + answer);
            System.out.println("Odpowiedz prawidlowa: " + currentObsv.type);
            if (!needsSmallerK) {
                System.out.println("kompatybilnosc (dla K=" + k + "): " + ((matching / k) * 100) + "%");
            } else {
                System.out.println("kompatybilnosc (dla K=" + localK + "): " + ((matching / localK) * 100) + "%");
            }
        }
        System.out.println();
        System.out.println("Dokladnosc: " + (correctAnswer / testObsv.size()) * 100 + "%");

    }
}
