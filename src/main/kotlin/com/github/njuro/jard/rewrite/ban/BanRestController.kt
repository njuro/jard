package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.ban.dto.BanDto
import com.github.njuro.jard.rewrite.ban.dto.BanForm
import com.github.njuro.jard.rewrite.ban.dto.UnbanForm
import com.github.njuro.jard.rewrite.common.API_ROOT_BANS
import com.github.njuro.jard.rewrite.common.PATH_VARIABLE_BAN
import com.github.njuro.jard.rewrite.config.security.methods.HasAuthorities
import com.github.njuro.jard.rewrite.user.UserAuthority.MANAGE_BANS
import com.github.njuro.jard.rewrite.user.UserAuthority.VIEW_IP
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping(API_ROOT_BANS)
class BanRestController(private val banFacade: BanFacade) {
    @PostMapping
    @HasAuthorities(MANAGE_BANS, VIEW_IP)
    fun createBan(@RequestBody banForm: @Valid BanForm): ResponseEntity<BanDto> =
        ResponseEntity.status(CREATED).body(banFacade.createBan(banForm))

    @GetMapping("/me")
    fun getOwnBan(request: HttpServletRequest): BanDto {
        return banFacade.getActiveBan(request.remoteAddr)
    }

    @HasAuthorities(MANAGE_BANS, VIEW_IP)
    @GetMapping
    fun getAllBans(): List<BanDto> = banFacade.getAllBans()

    @PutMapping(PATH_VARIABLE_BAN)
    @HasAuthorities(MANAGE_BANS, VIEW_IP)
    fun editBan(oldBan: BanDto, @RequestBody banForm: @Valid BanForm): BanDto =
        banFacade.editBan(oldBan, banForm)


    @PutMapping("$PATH_VARIABLE_BAN/unban")
    @HasAuthorities(MANAGE_BANS, VIEW_IP)
    fun unban(ban: BanDto, @RequestBody unbanForm: @Valid UnbanForm): BanDto =
         banFacade.unban(ban, unbanForm)

}
