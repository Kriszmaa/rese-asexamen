package resenas.resena.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import resenas.resena.model.Reseñas;

@Repository
public interface ReseñasRepository extends JpaRepository<Reseñas, Long> {

}
