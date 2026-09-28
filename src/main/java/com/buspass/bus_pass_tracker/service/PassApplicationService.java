package com.buspass.bus_pass_tracker.service;

import com.buspass.bus_pass_tracker.dto.ApprovalRequest;
import com.buspass.bus_pass_tracker.dto.PassApplicationRequest;
import com.buspass.bus_pass_tracker.dto.RejectionRequest;

import com.buspass.bus_pass_tracker.entity.ApplicationStatus;
import com.buspass.bus_pass_tracker.entity.BusRoute;
import com.buspass.bus_pass_tracker.entity.PassApplication;
import com.buspass.bus_pass_tracker.entity.PassModel;
import com.buspass.bus_pass_tracker.entity.Student;

import com.buspass.bus_pass_tracker.exception.BusinessRuleException;
import com.buspass.bus_pass_tracker.exception.ResourceNotFoundException;

import com.buspass.bus_pass_tracker.repository.BusRouteRepository;
import com.buspass.bus_pass_tracker.repository.PassApplicationRepository;
import com.buspass.bus_pass_tracker.repository.PassModelRepository;
import com.buspass.bus_pass_tracker.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassApplicationService {

    private final PassApplicationRepository applicationRepository;

    private final StudentRepository studentRepository;

    private final BusRouteRepository routeRepository;

    private final PassModelRepository modelRepository;


    public PassApplicationService(
            PassApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            BusRouteRepository routeRepository,
            PassModelRepository modelRepository) {

        this.applicationRepository =
                applicationRepository;

        this.studentRepository =
                studentRepository;

        this.routeRepository =
                routeRepository;

        this.modelRepository =
                modelRepository;
    }


    public PassApplication apply(
            PassApplicationRequest request) {

        Student student =
                studentRepository.findById(
                        request.getStudentId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + request.getStudentId()
                        )
                );


        BusRoute route =
                routeRepository.findById(
                        request.getRouteId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus route not found with id: "
                                        + request.getRouteId()
                        )
                );


        PassModel model =
                modelRepository.findById(
                        request.getModelId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pass model not found with id: "
                                        + request.getModelId()
                        )
                );


        PassApplication application =
                new PassApplication();


        application.setStudent(student);

        application.setBusRoute(route);

        application.setPassModel(model);

        application.setBoardingPoint(
                request.getBoardingPoint()
        );

        application.setPhotoReference(
                request.getPhotoReference()
        );

        application.setStatus(
                ApplicationStatus.PENDING
        );

        application.setAppliedAt(
                LocalDateTime.now()
        );


        return applicationRepository.save(
                application
        );
    }


    public PassApplication getById(Long id) {

        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with id: "
                                        + id
                        )
                );
    }


    public List<PassApplication> getAll() {

        return applicationRepository.findAll();
    }


    public List<PassApplication> getByStudent(
            Long studentId) {

        if (!studentRepository.existsById(studentId)) {

            throw new ResourceNotFoundException(
                    "Student not found with id: "
                            + studentId
            );
        }

        return applicationRepository
                .findByStudentIdOrderByAppliedAtDesc(
                        studentId
                );
    }


    public List<PassApplication> getByStatus(
            ApplicationStatus status) {

        return applicationRepository
                .findByStatusOrderByAppliedAtDesc(
                        status
                );
    }


    public PassApplication approve(
            Long applicationId,
            ApprovalRequest request) {


        PassApplication application =
                getById(applicationId);


        if (application.getStatus()
                != ApplicationStatus.PENDING) {

            throw new BusinessRuleException(
                    "Only pending applications can be approved"
            );
        }


        Long studentId =
                application.getStudent().getId();


        LocalDate today =
                LocalDate.now();


        // BUSINESS RULE:
        // Student cannot have another active pass.

        if (applicationRepository.existsActivePass(
                studentId,
                ApplicationStatus.APPROVED,
                today)) {

            throw new BusinessRuleException(
                    "This student already has an active bus pass"
            );
        }


        LocalDate endDate =
                today.plusDays(
                        request.getValidityDays() - 1L
                );


        application.setStatus(
                ApplicationStatus.APPROVED
        );


        application.setReviewedAt(
                LocalDateTime.now()
        );


        application.setAdminRemark(
                request.getAdminRemark()
        );


        application.setPassNumber(
                String.format(
                        "BP-%d-%06d",
                        today.getYear(),
                        applicationId
                )
        );


        application.setValidityStart(
                today
        );


        application.setValidityEnd(
                endDate
        );


        return applicationRepository.save(
                application
        );
    }


    public PassApplication reject(
            Long applicationId,
            RejectionRequest request) {


        PassApplication application =
                getById(applicationId);


        if (application.getStatus()
                != ApplicationStatus.PENDING) {

            throw new BusinessRuleException(
                    "Only pending applications can be rejected"
            );
        }


        application.setStatus(
                ApplicationStatus.REJECTED
        );


        application.setReviewedAt(
                LocalDateTime.now()
        );


        application.setRejectionReason(
                request.getRejectionReason()
        );


        application.setAdminRemark(
                request.getAdminRemark()
        );


        return applicationRepository.save(
                application
        );
    }


    public List<PassApplication> getExpiringPasses(
            int days) {


        if (days < 0) {

            throw new BusinessRuleException(
                    "Days cannot be negative"
            );
        }


        LocalDate today =
                LocalDate.now();


        return applicationRepository
                .findExpiringPasses(
                        ApplicationStatus.APPROVED,
                        today,
                        today.plusDays(days)
                );
    }
}