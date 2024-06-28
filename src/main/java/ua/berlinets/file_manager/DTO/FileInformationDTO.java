package ua.berlinets.file_manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInformationDTO {
    private String name;
    private String path;
    private boolean file;
    private boolean directory;
    private String size;
    private String lastModified;

}
