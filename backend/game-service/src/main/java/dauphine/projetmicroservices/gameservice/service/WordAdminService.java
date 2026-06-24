package dauphine.projetmicroservices.gameserver.service;

import dauphine.projetmicroservices.gameserver.dto.PagedWordsResponse;
import dauphine.projetmicroservices.gameserver.dto.WordCreateRequest;
import dauphine.projetmicroservices.gameserver.dto.WordDTO;
import dauphine.projetmicroservices.gameserver.dto.WordUpdateRequest;
import dauphine.projetmicroservices.gameserver.model.Word;
import dauphine.projetmicroservices.gameserver.repository.WordRepository;
import dauphine.projetmicroservices.gameserver.repository.WordSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordAdminService {

    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public PagedWordsResponse searchWords(String search, String difficulty, Integer minLength, Integer maxLength, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Word> resultPage = wordRepository.findAll(
                WordSpecifications.withFilters(
                        emptyToNull(search),
                        emptyToNull(difficulty),
                        minLength,
                        maxLength),
                pageable);

        return PagedWordsResponse.builder()
                .content(resultPage.getContent().stream().map(this::toDto).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .totalDictionarySize(wordRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public WordDTO getWord(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mot introuvable: " + id));
        return toDto(word);
    }

    @Transactional
    public WordDTO createWord(WordCreateRequest request) {
        String normalized = normalizeWord(request.getValue());
        if (wordRepository.findByValue(normalized).isPresent()) {
            throw new IllegalArgumentException("Le mot existe déjà: " + normalized);
        }

        Word word = Word.builder()
                .value(normalized)
                .language(resolveLanguage(request.getLanguage()))
                .difficulty(resolveDifficulty(normalized, request.getDifficulty()))
                .build();

        return toDto(wordRepository.save(word));
    }

    @Transactional
    public WordDTO updateWord(Long id, WordUpdateRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mot introuvable: " + id));

        if (request.getValue() != null && !request.getValue().isBlank()) {
            String normalized = normalizeWord(request.getValue());
            wordRepository.findByValue(normalized).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Le mot existe déjà: " + normalized);
                }
            });
            word.setValue(normalized);
        }
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            word.setLanguage(request.getLanguage().trim().toUpperCase());
        }
        if (request.getDifficulty() != null && !request.getDifficulty().isBlank()) {
            word.setDifficulty(request.getDifficulty().trim().toUpperCase());
        } else if (request.getValue() != null) {
            word.setDifficulty(computeDifficulty(word.getValue()));
        }

        return toDto(wordRepository.save(word));
    }

    @Transactional
    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) {
            throw new IllegalArgumentException("Mot introuvable: " + id);
        }
        try {
            wordRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Impossible de supprimer ce mot : il est utilisé par des parties existantes");
        }
    }

    private WordDTO toDto(Word word) {
        return WordDTO.builder()
                .id(word.getId())
                .value(word.getValue())
                .language(word.getLanguage())
                .difficulty(word.getDifficulty())
                .length(word.getValue() != null ? word.getValue().length() : 0)
                .build();
    }

    private String normalizeWord(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le mot ne peut pas être vide");
        }
        String normalized = value.trim().toUpperCase().replaceAll("[^A-Z]", "");
        if (normalized.length() < 4 || normalized.length() > 15) {
            throw new IllegalArgumentException("Le mot doit contenir entre 4 et 15 lettres (A-Z)");
        }
        return normalized;
    }

    private String resolveLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "FR";
        }
        return language.trim().toUpperCase();
    }

    private String resolveDifficulty(String value, String difficulty) {
        if (difficulty != null && !difficulty.isBlank()) {
            return difficulty.trim().toUpperCase();
        }
        return computeDifficulty(value);
    }

    private String computeDifficulty(String value) {
        int len = value.length();
        if (len <= 6) {
            return "EASY";
        }
        if (len <= 8) {
            return "MEDIUM";
        }
        return "HARD";
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
