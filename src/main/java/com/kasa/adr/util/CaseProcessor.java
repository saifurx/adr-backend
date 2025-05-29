package com.kasa.adr.util;

import com.kasa.adr.model.Address;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.Loan;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

@Component
public class CaseProcessor {

    Logger logger = LoggerFactory.getLogger(CaseProcessor.class);

    public List<Case> processCases(String yamlStr, String inputCsvFile, String errorCsvFile) throws IOException {
        logger.info("Processing csv file ti List<Case>");
        List<Case> cases = new ArrayList<>();

            // Load YAML mapping
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(yamlStr);

            @SuppressWarnings("unchecked")
            Map<String, String> yamlMapping = (Map<String, String>) yamlData.get("headerMapping");

            // Parse CSV manually to handle nested objects
            List<Case> errorCases = new ArrayList<>();
            try (CSVReader csvReader = new CSVReader(new FileReader(inputCsvFile));
                 CSVWriter csvWriter = new CSVWriter(new FileWriter(errorCsvFile))) {

                // Load YAML mapping

                String[] headers = csvReader.readNext();  // Read the header row
                Map<String, Integer> headerIndexMap = mapHeaders(headers);

                List<String[]> errorRecords = new ArrayList<>();  // Store error records

                String[] row;
                while ((row = csvReader.readNext()) != null) {
                    Case caseObj = mapRowToCase(row, headerIndexMap, yamlMapping);

                    // Check for missing email or mobile
//                    if (caseObj.getEmail() == null || caseObj.getEmail().isEmpty() ||
//                            caseObj.getMobile() == null || caseObj.getMobile().isEmpty()) {
//                        errorRecords.add(row);
//                    } else {
                        cases.add(caseObj);
                   // }
                }

                // Write error records to error CSV
                if (!errorRecords.isEmpty()) {
                    csvWriter.writeNext(headers);  // Write header row
                    csvWriter.writeAll(errorRecords);  // Write error rows
                }

                // Print mapped cases
                cases.forEach(System.out::println);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            }


        return cases;
    }

        private Map<String, String> loadYamlMapping (String yamlFile) throws IOException {
            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(yamlFile)) {
                return yaml.load(inputStream);
            }
        }

        private Map<String, Integer> mapHeaders (String[]headers){
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndexMap.put(headers[i], i);
            }
            return headerIndexMap;
        }

        private Case mapRowToCase (String[]
        row, Map < String, Integer > headerIndexMap, Map < String, String > yamlMapping){
            Case caseObj = Case.builder().build();
            Address address = Address.builder().build();
            List<Loan> loans = new ArrayList<>(Arrays.asList(Loan.builder().build(), Loan.builder().build())); // Initialize two loans

            yamlMapping.forEach((csvHeader, fieldPath) -> {
                String value = getValue(row, headerIndexMap, csvHeader);
                assignField(caseObj, address, loans, fieldPath, value);
            });

            caseObj.setAddress(address);
            caseObj.setLoans(loans);
            return caseObj;
        }

        private String getValue (String[]row, Map < String, Integer > headerIndexMap, String header){
            Integer index = headerIndexMap.get(header);
            return (index != null && index < row.length) ? row[index].trim() : null;
        }

        private void assignField (Case caseObj, Address address, List < Loan > loans, String fieldPath, String value){
            if (fieldPath.startsWith("loans[")) {
                int loanIndex = getLoanIndex(fieldPath);
                String loanField = getLoanField(fieldPath);
                if (loanIndex < loans.size()) {
                    Loan loan = loans.get(loanIndex);
                    assignLoanField(loan, loanField, value);
                }
            } else if (fieldPath.startsWith("address.")) {
                String addressField = fieldPath.substring("address.".length());
                assignAddressField(address, addressField, value);
            } else {
                assignCaseField(caseObj, fieldPath, value);
            }
        }

        private int getLoanIndex (String fieldPath){
            int start = fieldPath.indexOf('[') + 1;
            int end = fieldPath.indexOf(']');
            return Integer.parseInt(fieldPath.substring(start, end));
        }

        private String getLoanField (String fieldPath){
            return fieldPath.substring(fieldPath.indexOf(']') + 2);
        }

        private void assignLoanField (Loan loan, String field, String value){
            switch (field) {
                case "loanNumber" -> loan.setLoanNumber(value);
                case "amount" -> loan.setAmount(value);
                case "type" -> loan.setType(value);
                case "emi" -> loan.setEmi(value);
                case "tenure" -> loan.setTenure(value);
                case "disbursalDate" -> loan.setDisbursalDate(value);
                case "additionalDetails" -> loan.setAdditionalDetails(value);
                case "carRegNumber" -> loan.setCarRegNumber(value);
                case "engineNumber" -> loan.setEngineNumber(value);
                case "chassisNumber" -> loan.setChassisNumber(value);
                case "assetDescription" -> loan.setAssetDescription(value);
                case "make" -> loan.setMake(value);
                default -> System.err.println("Unknown loan field: " + field);
            }
        }

        private void assignAddressField (Address address, String field, String value){
            switch (field) {
                case "line1" -> address.setLine1(value);
                case "line2" -> address.setLine2(value);
                case "location" -> address.setLocation(value);
                case "district" -> address.setDistrict(value);
                case "state" -> address.setState(value);
                case "country" -> address.setCountry(value);
                case "pin" -> address.setPin(value);
                default -> System.err.println("Unknown address field: " + field);
            }
        }

        private void assignCaseField (Case caseObj, String field, String value){
            switch (field) {
                case "_id" -> caseObj.setId(value);
                case "customerId" -> caseObj.setCustomerId(value);
                case "name" -> caseObj.setName(value);
                case "mobile" -> caseObj.setMobile(value);
                case "email" -> caseObj.setEmail(value);
                case "loanRecallNoticeDate" -> caseObj.setLoanRecallNoticeDate(value);
                case "LRNAmount" -> caseObj.setLRNAmount(value);
                case "loanRecallNoticeNumber" -> caseObj.setLoanRecallNoticeNumber(value);
                case "KASAAppointmentDate" -> caseObj.setKASAAppointmentDate(value);
                case "invocationRefNo" -> caseObj.setInvocationRefNo(value);
                case "appearanceDatePlus1day" -> caseObj.setAppearanceDatePlus1day(value);

                default -> System.err.println("Unknown case field: " + field);
            }
        }
    }
