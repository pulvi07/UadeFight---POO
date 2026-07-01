package uadefight.modelo;

// aca defino los estados en los q puede estar una batalla.
// me sirve para cortar el bucle de turnos cuando alguien gana o pierde.
// los pongo en mayuscula xq es como se usan los enums en java para el q habia preguntado

public enum EstadoBatalla {
    EN_CURSO,   
    VICTORIA,  
    DERROTA,    
    PAUSADA     
}
