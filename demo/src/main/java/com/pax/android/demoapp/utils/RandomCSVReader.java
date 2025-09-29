package com.pax.android.demoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pax.android.demoapp.dto.SalesRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomCSVReader {
    private static final String TAG = RandomCSVReader.class.getSimpleName();
    private static final String PREFS_NAME = "random_csv_reader_prefs";
    private static final String KEY_READ_RECORDS = "read_records";

    private final Context context;
    private final String fileName;
    private List<SalesRecord> allRecords;
    private final Set<Integer> readIndexes;
    private final Random random;
    private int totalRecords;

    public RandomCSVReader(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
        this.random = new Random();
        this.readIndexes = new HashSet<>();

        loadAllRecords();
        loadReadIndexes();
    }


    private void loadAllRecords() {
        allRecords = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = context.getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) {
                    lineCount++;
                    continue;
                }

                SalesRecord record = parseCSVLine(line, lineCount);
                if (record != null) {
                    allRecords.add(record);
                }
                lineCount++;
            }

            totalRecords = allRecords.size();

        } catch (IOException e) {
            Log.e(TAG, "errror: " + e);
            Log.e("RandomCSVReader", "load CSV file failed: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "errror: " + e);
            }
        }
    }

    /**
     * random read
     */
    public SalesRecord readRandomRecord() {
        if (allRecords == null || allRecords.isEmpty()) {
            Log.e("RandomCSVReader", "no usable records");
            return null;
        }

        if (readIndexes.size() >= totalRecords) {
            Log.d("RandomCSVReader", "read all records already");
            resetReadStatus();
        }

        int randomIndex;
        int maxAttempts = totalRecords * 2;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            randomIndex = random.nextInt(totalRecords);

            if (!readIndexes.contains(randomIndex)) {
                readIndexes.add(randomIndex);
                saveReadIndexes();

                SalesRecord record = allRecords.get(randomIndex);
                Log.d("RandomCSVReader", "read " + (randomIndex + 1) + " rocords");
                return record;
            }
        }

        Log.e("RandomCSVReader", "no unread records");
        return null;
    }

    /**
     * parse csv
     */
    private SalesRecord parseCSVLine(String line, int lineNumber) {
        try {
            List<String> values = parseCSVLine(line);

            if (values.size() >= 11) {
                SalesRecord record = new SalesRecord();
                record.setEventTime(values.get(0).trim());
                record.setSegment(values.get(1).trim());
                record.setCity(values.get(2).trim());
                record.setState(values.get(3).trim());
                record.setRegion(values.get(4).trim());
                record.setCategory(values.get(5).trim());
                record.setProduct(values.get(6).trim());

                try {
                    record.setSales(Double.parseDouble(values.get(7).trim()));
                    record.setQuantity(Integer.parseInt(values.get(8).trim()));
                    record.setDiscount(Double.parseDouble(values.get(9).trim()));
                    record.setProfit(Double.parseDouble(values.get(10).trim()));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "errror: " + e);
                    return null;
                }

                record.setLineNumber(lineNumber);
                return record;
            }
        } catch (Exception e) {
            Log.e(TAG, "errror: " + e);
        }

        return null;
    }


    private List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());

        return result;
    }


    public void resetReadStatus() {
        readIndexes.clear();
        saveReadIndexes();
        Log.d("RandomCSVReader", "reset read status");
    }


    public int getReadCount() {
        return readIndexes.size();
    }


    public int getTotalRecords() {
        return totalRecords;
    }


    private void loadReadIndexes() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String readIndexesString = prefs.getString(KEY_READ_RECORDS, "");

        if (!readIndexesString.isEmpty()) {
            String[] indexes = readIndexesString.split(",");
            for (String index : indexes) {
                try {
                    readIndexes.add(Integer.parseInt(index));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "errror: " + e);
                }
            }
        }
        Log.d("RandomCSVReader", "already add  " + readIndexes.size());
    }

    private void saveReadIndexes() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (Integer index : readIndexes) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(index);
        }

        editor.putString(KEY_READ_RECORDS, sb.toString());
        editor.apply();
    }


    public String getStatistics() {
        return String.format("total: %d, already read: %d, unread: %d", totalRecords, readIndexes.size(), totalRecords - readIndexes.size());
    }
}