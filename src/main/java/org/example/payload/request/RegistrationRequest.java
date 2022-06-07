package org.example.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.annotations.ValidPassword;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
	@NotBlank(message = "Введіть ім`я")             private String name;
	@NotBlank(message = "Оберіть роль")             private String roleName;
	@NotBlank(message = "Введіть прізвище")         private String surname;
	@ValidPassword(message = "Невірний пароль")     private String temporaryPassword;
	@Size(min=5, max=15, message = "Юзернейм повинен містити від 5 до 15 символів")
	@NotBlank(message = "Введіть юзернейм")         private String username;
	@NotBlank(message = "Введіть ім`я по-батькові") private String patronymic;
}
