package jj;

import java.io.InputStreamReader;

public interface UserType{
     String typeName(); // Имя типа
     Object create(); // Создает объект ИЛИ
     Object clone(); // Клонирует текущий
     Object readValue(InputStreamReader in); // Создает и читает объект
     Object parseValue(String ss); // Создает и парсит содержимое из строки
     Comparator getTypeComparator(); // Возвращает компаратор для сравнения
}