package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.FaqDTO;
import com.primeroute.logistics.entity.Faq;
import com.primeroute.logistics.repository.FaqRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Transactional(readOnly = true)
    public List<FaqDTO> getActiveFaqs(String category) {
        List<Faq> faqs;
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("all")) {
            faqs = faqRepository.findByCategoryIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(category.trim());
        } else {
            faqs = faqRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        }
        return faqs.stream().map(FaqDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return faqRepository.findDistinctActiveCategories();
    }
}
