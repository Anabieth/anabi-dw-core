package lv.llu.science.utils;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface ExMongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
    Optional<T> findById(ID id);
}
