package com.project.service.impl;

import com.project.dto.*;
import com.project.entity.Patient;
import com.project.entity.Vaccination;
import com.project.entity.VaccinationHistory;
import com.project.entity.Vaccine;
import com.project.repository.*;
import com.project.service.VaccinationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class VaccinationHistoryServiceImpl implements VaccinationHistoryService {

    @Autowired
    private VaccinationHistoryRepository vaccinationHistoryRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private VaccinationRepository vaccinationRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private VaccinationTypeRepository vaccinationTypeRepository;

    /**
     * tuNH
     **/
    @Override
    public Page<VaccinationHistory> getAllVaccinationHistory(String vaccineName, String vaccinationDate, int patientId, Pageable pageable) {
        return this.vaccinationHistoryRepository.findAllByVaccination_Vaccine_NameContainingAndVaccination_DateContainingAndPatient_PatientId(vaccineName, vaccinationDate, patientId, pageable);
    }

    /**
     * tuNH
     **/
    @Override
    public VaccinationHistoryFeedbackDTO findByIdVaccinationHistory(Integer vaccinationHistoryId) {
        return this.vaccinationHistoryRepository.findByIdVaccinationHistory(vaccinationHistoryId);
    }

    /**
     * tuNH
     **/
    @Override
    public void updateVaccinationHistory(Integer vaccinationHistoryId, VaccinationHistorySendFeedbackDTO vaccinationHistorySendFeedbackDTO) {
        this.vaccinationHistoryRepository.updateFeedbackVaccinationHistory(vaccinationHistoryId, vaccinationHistorySendFeedbackDTO.getAfterStatus());
    }

    /**
     * tuNH
     **/
    @Override
    public VaccinationHistoryGetAfterStatusDTO getAfterStatusVaccinationHistory(Integer vaccinationHistoryId) {
        return this.vaccinationHistoryRepository.getAfterStatus(vaccinationHistoryId);
    }

    /**
     * Phuoc: T???o m???i l???ch ti??m theo y??u c???u
     **/
    @Override
    public VaccinationHistory registerVaccinationHistory(VaccinationHistory vaccinationHistoryTemp) {
        return vaccinationHistoryRepository.save(vaccinationHistoryTemp);
    }

    /**
     * Made by Khanh lay list history
     */
    @Override
    public List<VaccinationHistory> findAll() {
        return this.vaccinationHistoryRepository.findAll();
    }

    /**
     * Nguyen Van Linh: Get all email of patient to remind vaccination
     */
    @Override
    public List<String> getAllEmailToSend() {
        return vaccinationHistoryRepository.getAllEmailToSend();
    }

    /**
     *Nguyen Van Linh
     */
    @Override
    public List<String> getEmailToSendOfVaccinationMore() {
        return vaccinationHistoryRepository.getEmailToSendOfVaccinationMore();
    }

    /**
     * LuyenNT
     *
     * @param name
     * @param status
     * @return
     */
    @Override
    public Page<VaccinationHistory> searchPeriodicVaccination(String name, Boolean status, Pageable pageable) {
        return vaccinationHistoryRepository.findAllByPatient_NameContainingAndVaccination_StatusIsAndVaccination_VaccinationType_VaccinationTypeId(name, status,1, pageable);
    }
    /**
     * LuyenNT code
     *
     * @param name
     * @param pageable
     * @return
     */
    @Override
    public Page<VaccinationHistory> searchNoStatusPeriodicVaccination(String name, Pageable pageable) {
        return vaccinationHistoryRepository.findAllByPatient_NameContainingAndVaccination_VaccinationType_VaccinationTypeId(name,1, pageable);
    }
    /**
     * LuyenNT
     * @param pageable
     * @return
     */
    @Override
    public Page<VaccinationHistory> finAllPeriodicVaccination(Pageable pageable) {
        return vaccinationHistoryRepository.findAllByVaccination_VaccinationType_VaccinationTypeId(1,pageable);
    }

    /**
     * list:  create by LongBP
     */
    @Override
    public Page<VaccinationHistory> getAllRegisteredRequired(String name,Integer id, Pageable pageable) {
        return this.vaccinationHistoryRepository.findAllByPatient_NameContainingAndVaccination_VaccinationType_VaccinationTypeId(name,id, pageable);
    }


    /**
     * search and paging:  create by LongBP
     **/
    @Override
    public Page<VaccinationHistory> searchNameAndInjected(String name,Integer id, Boolean status, Pageable pageable) {
        return this.vaccinationHistoryRepository.findAllByPatient_NameContainingAndVaccination_VaccinationType_VaccinationTypeIdAndStatusIs(name, id, status, pageable);
    }

    /**
     * find by id:  create by LongBP
     **/
    @Override
    public List<VaccinationHistoryRegisteredDTO> findId(Integer id) {
        return this.vaccinationHistoryRepository.findId(id);
    }

    /**
     * edit by id:  create by LongBP
     **/
    @Override
    public void updateStatusVaccinationHistory(Boolean status, String preStatus, Integer id) {
        vaccinationHistoryRepository.updateVaccinationHistoryStatus(status, preStatus, id);
    }
     /** KhoaTA
     * Cancel periodical vaccination register
     */
    @Override
    public void cancelRegister(int vaccinationId, int patientId) {
        this.vaccinationHistoryRepository.cancelRegister(vaccinationId, patientId);
    }
     /**
     * TuNH:  sendMailFeedbackForAdmin
     **/
    @Override
    public void sendMailFeedbackForAdmin(String value, String accountEmail) throws MessagingException, UnsupportedEncodingException {
        String subject = "H??y x??c th???c email c???a b???n";
        String mailContent = "";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo("nguyenhoangtu24061999@gmail.com");
        helper.setFrom("vanlinh12b5@gmail.com", "TRUNG T??M Y T??? D??? PH??NG ???? N???NG");
        helper.setSubject(subject);
        mailContent = "<p sytle='color:red;'>Ph???n h???i t??? ng?????i d??ng ,<p>"
                + "<p sytle='color:red;'>N???i dung:" + value + "<p>"
                + "<p sytle='color:red;'> B???n c?? m???t email ph???n h???i t??? " + accountEmail + "<p>"
                + "<p>TRUNG T??M Y T??? D??? PH??NG ???? N???NG</p>";
        helper.setText(mailContent, true);
        javaMailSender.send(message);
    }

    @Override
    public void sendMail(VaccinationByRequestDTO vaccinationByRequestDTO, Patient patientTemp, Vaccine vaccineTemp, Vaccination vaccination) throws MessagingException, UnsupportedEncodingException {
        StringBuilder randomCode = new StringBuilder();
        randomCode.append(vaccination.getVaccinationId()).append("|").append(vaccinationByRequestDTO.getPatientId());
        String subject = "Th??ng tin ????ng k?? ti??m ch???ng c???a b???n";
        String mailContent = "";
        String cancelRegisterUrl = "http://localhost:4200/cancel-register?code=" + randomCode;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(patientTemp.getEmail());
        helper.setFrom("vanlinh12b5@gmail.com","TRUNG T??M Y T??? D??? PH??NG ???? N???NG");
        helper.setSubject(subject);
        mailContent = "<span style=\"font-weight: bold\">Xin ch??o<span> "+patientTemp.getGuardian()+",</span></span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> "+patientTemp.getName()+"</span> <span>v???a ???????c ????ng k?? ti??m ch???ng ?????nh k??? v???i th??ng tin sau:</span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> Ng??y ti??m ch???ng: </span><span>"+vaccinationByRequestDTO.getDateVaccination()+"</span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> Gi??? ti??m ch???ng: </span><span>"+vaccination.getStartTime()+"  - "+vaccination.getEndTime()+"</span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> ?????a ??i???m: </span><span>"+vaccination.getLocation().getName()+" </span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> T??n V???c xin: </span><span>"+vaccineTemp.getName()+" </span>\n" +
                "<br><br>\n" +
                "<span style=\"font-weight: bold\"> Xu???t x???: </span><span>"+vaccineTemp.getOrigin()+" </span>\n" +
                "<br><br>\n" +
                "<p style=\"font-style: italic; color: red\">Trong tr?????ng h???p b???n kh??ng th??? tham gia v?? l?? do n??o ????, b???n c?? th??? h???y ????ng k?? b???ng link b??n d?????i:</p>\n" +
                "<h3><a href='" + cancelRegisterUrl + "'>Link h???y ????ng k??!</a></h3>" +
                "<p>TRUNG T??M Y T??? D??? PH??NG ???? N???NG K??NH B??O</p>";
        helper.setText(mailContent, true);
        javaMailSender.send(message);

    }

    /**
     * KhoaTA
     * Save new register for a vaccination
     */
    @Override
    public VaccinationHistory createNewRegister(PeriodicalVaccinationTempRegisterDTO register) {
        Vaccination firstVaccination = this.vaccinationRepository.getOne(register.getVaccinationId());
        Patient patient = this.patientRepository.getOne(register.getPatientId());
        for( int i = 1; i < firstVaccination.getVaccine().getTimes(); i++) {
            VaccinationHistory vaccinationHistory = new VaccinationHistory();
            vaccinationHistory.setPatient(patient);
            vaccinationHistory.setStartTime(register.getStartTime());
            vaccinationHistory.setEndTime(register.getEndTime());
            vaccinationHistory.setDeleteFlag(false);
            Vaccination repeatVaccination = new Vaccination();
            repeatVaccination.setVaccine(firstVaccination.getVaccine());
            repeatVaccination.setDeleteFlag(false);
            repeatVaccination.setLocation(firstVaccination.getLocation());
            repeatVaccination.setDescription(firstVaccination.getDescription());
            repeatVaccination.setStatus(false);
            repeatVaccination.setStartTime(firstVaccination.getStartTime());
            repeatVaccination.setEndTime(firstVaccination.getEndTime());
            repeatVaccination.setLocation(this.locationRepository.getOne(firstVaccination.getLocation().getLocationId()));
            repeatVaccination.setVaccinationType(this.vaccinationTypeRepository.getOne(2));
            repeatVaccination.setDescription("Ti??m nh???c");

            LocalDate vaccinationDate = LocalDate.parse(firstVaccination.getDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            repeatVaccination.setDate(vaccinationDate.plusDays(i*firstVaccination.getVaccine().getDuration()).toString());
            vaccinationHistory.setVaccination(this.vaccinationRepository.save(repeatVaccination));
            vaccinationHistory.setVaccinationTimes(i+1);
            System.out.println(vaccinationHistory.toString());
            vaccinationHistoryRepository.save(vaccinationHistory);
        }
        VaccinationHistory vaccinationHistory = new VaccinationHistory();
        vaccinationHistory.setPatient(patient);
        vaccinationHistory.setVaccination(firstVaccination);
        vaccinationHistory.setStartTime(register.getStartTime());
        vaccinationHistory.setEndTime(register.getEndTime());
        vaccinationHistory.setDeleteFlag(false);
        vaccinationHistory.setVaccinationTimes(1);
        return this.vaccinationHistoryRepository.save(vaccinationHistory);
    }

    /**
     * Phuoc
     **/
    @Override
    public Page<VaccinationHistory> findAllByPatientId(Integer patientId, boolean b, Pageable pageable) {
        return vaccinationHistoryRepository.findAllByPatient_PatientIdAndDeleteFlag(patientId, b, pageable);
    }

    @Override
    public Page<VaccinationHistory> findAllByPatient_PatientIdAndVaccination_Vaccine_NameContainingAndVaccination_Date(Integer patient_patientId, String vaccination_vaccine_name, String vaccination_date,Boolean b, Pageable pageable) {
        return vaccinationHistoryRepository.findAllByPatient_PatientIdAndVaccination_Vaccine_NameContainingAndVaccination_DateAndDeleteFlag(patient_patientId, vaccination_vaccine_name, vaccination_date, b, pageable);
    }

    @Override
    public Page<VaccinationHistory> findAllByPatient_PatientIdAndVaccination_Vaccine_NameContainingAndDeleteFlag(Integer patient_patientId, String vaccination_vaccine_name, Boolean deleteFlag, Pageable pageable) {
        return vaccinationHistoryRepository.findAllByPatient_PatientIdAndVaccination_Vaccine_NameContainingAndDeleteFlag(patient_patientId, vaccination_vaccine_name, deleteFlag, pageable);
    }

    @Override
    public List<VaccinationHistory> findAllByVaccinationTransactionIsNull() {
        return vaccinationHistoryRepository.findAllByVaccinationTransactionIsNull();
    }

    @Override
    public Integer getAllVaccinationByDate(String date, boolean b) {
        return vaccinationHistoryRepository.countAllByVaccination_DateAndDeleteFlag(date, b);
    }

    @Override
    public Integer getAllVaccinationByDate(String date, String time, boolean b) {
        return vaccinationHistoryRepository.countAllByVaccination_DateAndStartTimeAndDeleteFlag(date, time, b);
    }


}
