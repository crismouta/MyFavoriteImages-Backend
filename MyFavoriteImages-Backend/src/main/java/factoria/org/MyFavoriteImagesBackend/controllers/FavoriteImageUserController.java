package factoria.org.MyFavoriteImagesBackend.controllers;

import factoria.org.MyFavoriteImagesBackend.domain.models.FavoriteImageUser;
import factoria.org.MyFavoriteImagesBackend.domain.services.FavoriteImageUserService;
import factoria.org.MyFavoriteImagesBackend.infra.dtos.UserDto;
import factoria.org.MyFavoriteImagesBackend.infra.dtos.converters.UserDtoToUserConverter;
import factoria.org.MyFavoriteImagesBackend.infra.dtos.converters.UserToUserDtoConverter;
import factoria.org.MyFavoriteImagesBackend.infra.results.Result;
import factoria.org.MyFavoriteImagesBackend.infra.results.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class FavoriteImageUserController {
    private final FavoriteImageUserService userService;

    private final UserToUserDtoConverter userToUserDtoConverter;

    private final UserDtoToUserConverter userDtoToUserConverter;

    public FavoriteImageUserController(FavoriteImageUserService userService, UserToUserDtoConverter userToUserDtoConverter, UserDtoToUserConverter userDtoToUserConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Long userId){
        FavoriteImageUser foundUser = this.userService.findById(userId);
        UserDto userDto = this.userToUserDtoConverter.convert(foundUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
    }

    @GetMapping
    public Result findAllUsers() {
        List<FavoriteImageUser> foundUsers = this.userService.findAll();
        List<UserDto> usersDto = foundUsers.stream()
                .map(this.userToUserDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", usersDto);
    }

    @PostMapping
    public Result addUser(@RequestBody @Valid UserDto userDto) {
        FavoriteImageUser newUser = this.userDtoToUserConverter.convert(userDto);
        FavoriteImageUser savedUser = this.userService.save(newUser);

        UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto );
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        FavoriteImageUser updatedUser = this.userDtoToUserConverter.convert(userDto);
        FavoriteImageUser result = this.userService.update(userId, updatedUser);

        UserDto updatedUserDto = this.userToUserDtoConverter.convert(result);
        return new Result(true, StatusCode.SUCCESS, "Updated Success", updatedUserDto);
    }
}