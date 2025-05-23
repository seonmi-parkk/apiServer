package kr.co.apiserver.repository;

import kr.co.apiserver.domain.Todo;
import kr.co.apiserver.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {

}
