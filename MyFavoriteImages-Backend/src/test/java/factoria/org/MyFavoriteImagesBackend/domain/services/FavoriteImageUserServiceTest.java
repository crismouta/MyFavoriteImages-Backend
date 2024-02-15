package factoria.org.MyFavoriteImagesBackend.domain.services;

import factoria.org.MyFavoriteImagesBackend.domain.models.FavoriteImage;
import factoria.org.MyFavoriteImagesBackend.domain.models.FavoriteImageUser;
import factoria.org.MyFavoriteImagesBackend.infra.exceptions.ObjectNotFoundException;
import factoria.org.MyFavoriteImagesBackend.infra.persistence.FavoriteImageUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteImageUserServiceTest {
    @Mock
    FavoriteImageUserRepository userRepository;

    @InjectMocks
    FavoriteImageUserService userService;

    List<FavoriteImageUser> users;

    @BeforeEach
    void setUp() {
        FavoriteImageUser user1 = new FavoriteImageUser();
        user1.setId(1L);
        user1.setUsername("User1");
        user1.setPassword("123456");
        user1.setEnabled(true);
        user1.setRoles("user");

        FavoriteImageUser user2 = new FavoriteImageUser();
        user2.setId(2L);
        user2.setUsername("User2");
        user2.setPassword("654321");
        user2.setEnabled(true);
        user2.setRoles("admin");

        this.users = new ArrayList<>();
        this.users.add(user1);
        this.users.add(user2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldFindByIdSuccessfully() {
        FavoriteImage image1 = new FavoriteImage();
        image1.setId(Long.valueOf("1"));
        image1.setTitle("Image 1");
        image1.setDescription("Description image 1");
        image1.setUrl("image1 URL");

        FavoriteImageUser user = new FavoriteImageUser();
        user.setId(1L);
        user.setUsername("User 1");
        user.setPassword("123456");
        user.setEnabled(true);
        user.setRoles("user");
        user.addImage(image1);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        //When
        FavoriteImageUser returnedUser = userService.findById(1L);

        //Then
        assertThat(returnedUser.getId()).isEqualTo(1L);
        assertThat(returnedUser.getUsername()).isEqualTo("User 1");
        assertThat(returnedUser.getPassword()).isEqualTo("123456");
        assertThat(returnedUser.isEnabled()).isEqualTo(true);
        assertThat(returnedUser.getRoles()).isEqualTo("user");
        assertThat(returnedUser.getImages()).isNotEmpty();
        assertThat(returnedUser.getImages()).containsExactly(image1);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrownErrorWhenUserNotFound() {
        //Given
        given(userRepository.findById(Mockito.any(Long.class))).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(()-> {
            FavoriteImageUser returnedUser = userService.findById(1L);
        });

        //Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class).hasMessage("Could not find user with id: 1");
        verify(userRepository, times(1)).findById(Long.valueOf("1"));
    }

    @Test
    void shouldFindAllSuccessfully() {
        //Given
        given(userRepository.findAll()).willReturn(this.users);

        //When
        List<FavoriteImageUser> actualUsers = userService.findAll();

        //Then
        assertThat(actualUsers.size()).isEqualTo(this.users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldSaveSuccessfully(){
        //Given
        FavoriteImageUser newUser = new FavoriteImageUser();
        newUser.setUsername("New User");
        newUser.setPassword("987654");
        newUser.setEnabled(true);
        newUser.setRoles("user");

        given(userRepository.save(newUser)).willReturn(newUser);

        //When
        FavoriteImageUser savedUser = userService.save(newUser);

        //Then
        assertThat(savedUser.getUsername()).isEqualTo("New User");
        assertThat(savedUser.isEnabled()).isEqualTo(true);
        assertThat(savedUser.getRoles()).isEqualTo("user");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void shouldUpdateSuccessfully() {
        //Given
        FavoriteImageUser oldUser = new FavoriteImageUser();
        oldUser.setUsername("New User 1");
        oldUser.setEnabled(true);
        oldUser.setRoles("user");

        FavoriteImageUser updatedUser = new FavoriteImageUser();
        updatedUser.setUsername("New User 1 updated");
        updatedUser.setEnabled(true);
        updatedUser.setRoles("user");

        given(userRepository.findById(1L)).willReturn(Optional.of(oldUser));
        given(userRepository.save(oldUser)).willReturn(oldUser);

        //When
        FavoriteImageUser result = userService.update(1L, updatedUser);

        //Then
        assertThat(result.getId()).isEqualTo(updatedUser.getId());
        assertThat(result.getUsername()).isEqualTo(updatedUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(oldUser);
    }

    @Test
    void shouldThrownErrorWithNonExistentIdWhenUpdate() {
        //Given
        FavoriteImageUser updatedUser = new FavoriteImageUser();
        updatedUser.setUsername("New User 1 updated");
        updatedUser.setEnabled(true);
        updatedUser.setRoles("user");

        given(userRepository.findById(1L)).willReturn(Optional.empty());

        //When
        assertThrows(ObjectNotFoundException.class, () -> {
            userService.update(1L, updatedUser);
        });

        //Then
        verify(userRepository, times(1)).findById(1L);
    }
}