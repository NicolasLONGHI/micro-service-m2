package dauphine.projetmicroservices.gameserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedWordsResponse {
    private List<WordDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private long totalDictionarySize;
}
