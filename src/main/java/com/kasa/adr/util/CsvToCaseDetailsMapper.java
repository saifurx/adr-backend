package com.kasa.adr.util;

import com.kasa.adr.model.CaseDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CsvToCaseDetailsMapper {

    private static final Logger logger = LoggerFactory.getLogger(CsvToCaseDetailsMapper.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<CaseDetails> mapCsvToCaseDetails(String filePath) throws IOException {
        List<CaseDetails> caseDetailsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Read header line
            String headerLine = br.readLine();
            if (headerLine == null) {
                logger.error("Empty CSV file: {}", filePath);
                throw new IOException("Empty CSV file");
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Assuming tab-separated values
                if (values.length < 5) { // Adjust based on your actual column count
                    logger.warn("Skipping malformed line: {}", line);
                    continue; // Skip malformed lines
                }

                CaseDetails caseDetails = CaseDetails.builder().build();

                // Customer Information
                caseDetails.setCustomerId(values[0]);
                caseDetails.setCustomerName(values[1]);
                caseDetails.setCustomerAddress(values[2]);
                caseDetails.setCustomerEmailAddress(values[3]);
                caseDetails.setCustomerContactNumber(values[4]);

                // Co-applicant Information
                caseDetails.setCoapplicantName(values[5]);
                caseDetails.setCoapplicantAddress(values[6]);
                caseDetails.setCoapplicantEmail(values[7]);
                caseDetails.setCoapplicantMobile(values[8]);

                // Product Information
                caseDetails.setProduct(values[9]);
                caseDetails.setAssetDescription1(values[10]);
                caseDetails.setMake(values[11]);
                caseDetails.setCarRegNumber1(values[12]);
                caseDetails.setEngineNumber1(values[13]);
                caseDetails.setChasisNumber1(values[14]);

                // Loan 1 Information
                caseDetails.setTenure1(parseInteger(values[15]));
                caseDetails.setLoanNumber1(values[16]);
                caseDetails.setDisbursalDate1(parseDate(values[17]));
                caseDetails.setAmountFinanceLan1(parseBigDecimal(values[18]));
                caseDetails.setEmiLan1(parseBigDecimal(values[19]));
                caseDetails.setPenaltyCharges1(parseBigDecimal(values[20]));
                caseDetails.setBounceCharges1(parseBigDecimal(values[21]));
                caseDetails.setFuturePrincipal1(parseBigDecimal(values[22]));

                // Loan 2 Information
                caseDetails.setLoanNumber2(values[23]);
                caseDetails.setDisbursalDate2(parseDate(values[24]));
                caseDetails.setAmountFinanceLan2(parseBigDecimal(values[25]));
                caseDetails.setEmiLan2(parseBigDecimal(values[26]));
                caseDetails.setLoan2PenaltyCharges(parseBigDecimal(values[27]));
                caseDetails.setLoan2BounceCharges(parseBigDecimal(values[28]));
                caseDetails.setLoan2FuturePrincipal(parseBigDecimal(values[29]));

                // Loan Recall and Legal Information
                caseDetails.setLoanRecallNoticeDate(parseDate(values[30]));
                caseDetails.setLrnAmount(parseBigDecimal(values[31]));
                caseDetails.setFcAmount(parseBigDecimal(values[32]));
                caseDetails.setInstallmentOverdue(parseBigDecimal(values[33]));
                caseDetails.setPendingEmis(parseInteger(values[34]));
                caseDetails.setTotalClaimAmount(parseBigDecimal(values[35]));
                caseDetails.setClaimAmountInWord(values[36]);
                caseDetails.setLoanRecallNoticeNumber(values[37]);

                // Legal Proceedings Dates
                caseDetails.setKasaAppointmentDate(parseDate(values[38]));
                caseDetails.setCommencementLetterDate(parseDate(values[39]));
                caseDetails.setInvocationRefNo(values[40]);
                caseDetails.setArbitrationNoticeDispatchDate(parseDate(values[41]));
                caseDetails.setAppearanceDate(parseDate(values.length > 42 ? values[42] : null));
                caseDetails.setStatementOfAccountDate(parseDate(values.length > 43 ? values[43] : null));
                caseDetails.setInterimOrderDate(parseDate(values.length > 44 ? values[44] : null));
                caseDetails.setEvidenceDate(parseDate(values.length > 45 ? values[45] : null));
                caseDetails.setAwardDate(parseDate(values.length > 46 ? values[46] : null));

                caseDetailsList.add(caseDetails);
            }
        }

        return caseDetailsList;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}