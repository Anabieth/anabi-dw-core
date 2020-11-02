package lv.llu.science.dwh.domain.input;

import lv.llu.science.utils.ExMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputSwampRepository extends ExMongoRepository<InputSwamp, String> {
}
