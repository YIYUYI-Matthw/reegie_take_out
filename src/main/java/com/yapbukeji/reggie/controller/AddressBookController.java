package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.yapbukeji.reggie.common.BaseContext;
import com.yapbukeji.reggie.common.CustomException;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.AddressBook;
import com.yapbukeji.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    private AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    /**
     * 添加用户地址
     *
     * @param addressBook 用户地址类对象
     * @return 添加结果
     */
    @PostMapping
    public ResData<String> addAddress(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId()); // setId在filter中调用了
        addressBookService.save(addressBook);
        return ResData.success("新增成功");
    }

    /**
     * 根据session中的userId查找地址
     *
     * @return 返回list地址集合
     */
    @GetMapping("/list")
    public ResData<List<AddressBook>> getList() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        List<AddressBook> addressBookList = addressBookService.list(wrapper);
        return ResData.success(addressBookList);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook 根据id封装的AB类
     * @return 更改结果
     */
    @PutMapping("/default")
    public ResData<String> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper_clear = new LambdaUpdateWrapper<>(); // 注意这里是updateWrapper
        // 把该用户下所有地址设定为非默认
        wrapper_clear.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper_clear.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper_clear);
        // 把指定id的地址设定为default
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return ResData.success("默认地址已更改");
    }

    @GetMapping("/default")
    public ResData<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> addressWrapper = new LambdaQueryWrapper<>();
        addressWrapper.and((wrapper) -> wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId()))
                .and((wrapper) -> wrapper.eq(AddressBook::getIsDefault, 1));
        AddressBook addressBook = addressBookService.getOne(addressWrapper);
        return ResData.success(addressBook);
    }

    /**
     * 根据id获得对应地址
     *
     * @param addressId 地址id：这个变量名自定义，但是要上下一致
     * @return 地址信息
     */
    @GetMapping("/{addressId}")
    public ResData<AddressBook> getAddress(@PathVariable Long addressId) {
        AddressBook addressBook = addressBookService.getById(addressId);
        if (addressBook == null)
            throw new CustomException("当前地址不存在");
        else
            return ResData.success(addressBook);
    }

    /**
     * 编辑地址信息
     *
     * @param addressBook json转更新后数据
     * @return 返回string
     */
    @PutMapping
    public ResData<String> editAddress(@RequestBody AddressBook addressBook) {
        AddressBook addressDB = addressBookService.getById(addressBook.getId());
        if (addressDB == null)
            throw new CustomException("当前地址不存在");
        else {
            addressBookService.updateById(addressBook);
            return ResData.success("更新成功");
        }
    }
}