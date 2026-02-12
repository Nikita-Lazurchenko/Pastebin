package pet.project.mapper;

public interface Mapper<T,F> {
    T mapFrom(F f);
}
