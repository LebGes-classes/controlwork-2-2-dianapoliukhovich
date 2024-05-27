package scr1;

import java.io.*;
import java.util.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;


public class Main {
    private static List<Program> getProgramList(){
        List<Program> programList = new ArrayList<>();
        String currentChannel = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/data.txt"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    // Это строка с названием канала, убираем # и сохраняем в переменную
                    currentChannel = line.substring(1).trim();
                } else {
                    // Это строка со временем и названием программы
                    String time = line.trim();
                    String name = bufferedReader.readLine().trim();
                    BroadcastsTime broadcastsTime = new BroadcastsTime(time);
                    Program program = new Program(currentChannel, broadcastsTime, name);
                    programList.add(program);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return programList;
    }
    public static void printNowPrograms(String nowTime, List<Program> programList){
        BroadcastsTime currentTime = new BroadcastsTime(nowTime);
        for (int i = 0; i < programList.size(); i++) {
            Program currentProgram = programList.get(i);
            Program nextProgram = (i + 1 < programList.size()) ? programList.get(i + 1) : null;

            if (currentProgram.getTime().equals(currentTime) ||
                    (currentProgram.getTime().before(currentTime) && (nextProgram == null || nextProgram.getTime().after(currentTime)))) {
                currentProgram.print();
            }
        }
    }
    public static void findProgramsByName(String nameToSearch, List<Program> programList) {
        programList.stream()
                .filter(program -> program.getName().equalsIgnoreCase(nameToSearch))
                .forEach(program -> program.print());
    }

    public static void findChannelNowPrograms(String channelName,String nowTime,List<Program> programList){
        List<Program> channelList = programList.stream()
                .filter(program -> program.getChannel().equalsIgnoreCase(channelName)).toList();
        printNowPrograms(nowTime,channelList);
    }
    public static void findChannelProgramsInTimeRange(String channelName, String startTime, String endTime, List<Program> programList) {
        BroadcastsTime start = new BroadcastsTime(startTime);
        BroadcastsTime end = new BroadcastsTime(endTime);

        programList.stream()
                .filter(program -> program.getChannel().equalsIgnoreCase(channelName))
                .filter(program -> (program.getTime().after(start) || program.getTime().equals(start))
                        && (program.getTime().before(end) || program.getTime().equals(end)))
                .forEach(program -> program.print());
    }

    public static void saveProgramListToXlsx(List<Program> programList, String outputPath) {
        // Создаем новую книгу Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        // создаем лист в книге
        XSSFSheet sheet = workbook.createSheet("Program List");

        // создаем заголовок
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Channel");
        header.createCell(1).setCellValue("Broadcast Time");
        header.createCell(2).setCellValue("Program Name");

        // заполняем лист данными
        int rowNum = 1;
        for (Program program : programList) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(program.getChannel());
            row.createCell(1).setCellValue(program.getTime().toString());
            row.createCell(2).setCellValue(program.getName());
        }

        // авто-ресайз колонок
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // записываем созданную книгу в файл
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // закрываем книгу после записи, чтобы освободить ресурсы
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        List<Program> programList = getProgramList();
        // Сортировка по каналу, затем по времени показа
        programList.sort(Comparator.comparing(Program::getChannel).thenComparing(Program::getTime));
        programList.forEach(program -> program.print());

        //Вывод текущих программ
        printNowPrograms("16:10",programList);

        //Поиск программы
        findProgramsByName("Последний мент. 12-я серия",programList);

        //Определенный канал + сейчас
        findChannelNowPrograms("Первый","16:10",programList);

        //Определенный канал + промежуток времени
        findChannelProgramsInTimeRange("Первый", "15:00", "16:10", programList);

        //TO XMLS
        //saveProgramListToXlsx(programList, "ProgramList.xlsx");

    }
}
