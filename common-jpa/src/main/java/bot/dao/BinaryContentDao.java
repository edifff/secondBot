package bot.dao;

import bot.entity.enams.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentDao extends JpaRepository<BinaryContent, Long> {
}
