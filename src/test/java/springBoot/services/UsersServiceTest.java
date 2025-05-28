package springBoot.services;


import com.webapp.springBoot.DTO.Admin.AddNewRoleUsersAppDTO;
import com.webapp.springBoot.DTO.Users.ListUsersDTO;
import com.webapp.springBoot.entity.Roles;
import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import com.webapp.springBoot.repository.RolesRepository;
import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.service.ImageUsersAppService;
import com.webapp.springBoot.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersAppRepository usersAppRepository;

    @Mock
    private ImageUsersAppService imageUsersAppService;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    UsersService usersService;


    @Test
    @DisplayName("Получение ListUsersDTO при обращении к бд, userService")
    void getUserByName_ReturnsValidDTO(){
        // given
        String name = "Kirill";
        int page = 0;
        List<UsersApp> list = List.of(
                new UsersApp(name, name,  16, "dywinar", "dfgdg"),
                new UsersApp(name, name,  10, "dywinar", "dfgdg")
        );
        doReturn(list).when(usersAppRepository).findByNameContainingIgnoreCase(eq(name), any(PageRequest.class));
        String nameImage = UUID.randomUUID().toString();
        String nameImage2 = UUID.randomUUID().toString();
        doReturn(nameImage).when(imageUsersAppService).getImageName(list.getFirst());
        doReturn(nameImage2).when(imageUsersAppService).getImageName(list.get(1));
        // when
        ListUsersDTO result = usersService.getUserByName(name, page);
        // then
        assertNotNull(result);
        assertEquals(name, result.getUserList().getFirst().getName());
        assertEquals(2, result.getUserList().size());
        assertEquals(name, result.getUserList().get(1).getName());
        assertEquals(nameImage, result.getUserList().getFirst().getNameImage());
        assertEquals(nameImage2, result.getUserList().get(1).getNameImage());
    }

    @DisplayName("Проверка удаления прав у пользователя")
    @Test
    public void deleteRolesUsersApp_CheckOnceSave() throws ValidationErrorWithMethod {
        // given
        AddNewRoleUsersAppDTO addNewRoleUsersAppDTO = new AddNewRoleUsersAppDTO("nickname", "ADMIN");

        UsersApp usersApp = new UsersApp();
        Set<Roles> rolesSet = new HashSet<>();
        rolesSet.add(new Roles(0L, "ADMIN"));
        rolesSet.add(new Roles(1L, "USER"));
        usersApp.setRoles(rolesSet);
        doReturn(Optional.of("ADMIN")).when(rolesRepository).findByName("ADMIN");
        doReturn(Optional.of(usersApp)).when(usersAppRepository).findByNickname("nickname");
        doReturn(false).when(bindingResult).hasErrors();
        // when
        usersService.deleteRolesUsersApp(addNewRoleUsersAppDTO, bindingResult);
        // then
        verify(usersAppRepository, times(1)).save(usersApp);
        verifyNoMoreInteractions(usersAppRepository);
        assertFalse(usersApp.getRoles().stream().allMatch(st -> st.getName().equals("ADMIN")));




    }
}