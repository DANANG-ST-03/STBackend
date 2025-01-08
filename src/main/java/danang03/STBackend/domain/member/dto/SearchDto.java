package danang03.STBackend.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchDto {
    private int page;       // Current page number
    private int recordSize; // Number of records displayed per page
    private int pageSize;   // Number of pages displayed in the pagination bar

    public SearchDto() {
        this.page = 1;
        this.recordSize = 10;
        this.pageSize = 10;
    }

    public int getOffset() {
        return (page - 1) * recordSize;
    }

}
