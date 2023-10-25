package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyhmobile.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.CartBo;
import com.ruoyi.zlyyh.domain.vo.CartVo;
import com.ruoyi.zlyyh.domain.Cart;
import com.ruoyi.zlyyh.mapper.CartMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 购物车Service业务层处理
 *
 * @author yzg
 * @date 2023-10-16
 */
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements ICartService {

    private final CartMapper baseMapper;
    private final ProductMapper productMapper;

    /**
     * 查询购物车
     */
    @Override
    public CartVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询购物车列表
     */
    @Override
    public TableDataInfo<CartVo> queryPageList(CartBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Cart> lqw = buildQueryWrapper(bo);
        Page<CartVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<CartVo> cartVoTableDataInfo = TableDataInfo.build(result);
        List<CartVo> rows = cartVoTableDataInfo.getRows();
        for (CartVo row : rows) {
            ProductVo voById = productMapper.selectVoById(row.getProductId());
            if(voById.getStatus().equals("0") && ObjectUtil.isNotEmpty(voById.getSellEndDate()) && DateUtils.validTime(voById.getSellEndDate(),1)){
                voById.setStatus("1");
            }
            row.setProductVo(voById);
        }
        return cartVoTableDataInfo;
    }

    /**
     * 查询购物车列表
     */
    @Override
    public List<CartVo> queryList(CartBo bo) {
        LambdaQueryWrapper<Cart> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Cart> buildQueryWrapper(CartBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, Cart::getUserId, bo.getUserId());
        lqw.eq(bo.getProductId() != null, Cart::getProductId, bo.getProductId());
        return lqw;
    }

    /**
     * 新增购物车
     */
    @Override
    public Boolean insertByBo(CartBo bo) {
        // 是否存在，存在加数量
        CartVo voOne = baseMapper.selectVoOne(buildQueryWrapper(bo));
        if (ObjectUtil.isNotEmpty(voOne)) {
            //判断商品添加次数是否超过上限
            ProductVo productVo = productMapper.selectVoById(bo.getProductId());
            if (ObjectUtil.isNotEmpty(productVo.getLineUpperLimit())){
                if (voOne.getQuantity()+bo.getQuantity()>productVo.getLineUpperLimit()){
                    throw new ServiceException("该商品购物车内仅能添加"+productVo.getLineUpperLimit()+"次");
                }
            }
            bo.setQuantity(voOne.getQuantity() + bo.getQuantity());
            bo.setId(voOne.getId());
            return updateByBo(bo);
        }
        long count = baseMapper.selectCount(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, bo.getUserId()));
        if(count >=99){
            throw new ServiceException("购物车最多只能添加99件商品");
        }
        Cart add = BeanUtil.toBean(bo, Cart.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改购物车
     */
    @Override
    public Boolean updateByBo(CartBo bo) {
        Cart update = BeanUtil.toBean(bo, Cart.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Cart entity){
        //TODO 做一些数据校验,如唯一约束
        if (entity.getQuantity() < 1) {
            throw new ServiceException("数量错误");
        }
        ProductVo productVo = productMapper.selectVoById(entity.getProductId());
        if (ObjectUtil.isEmpty(productVo)) {
            throw new ServiceException("商品不存在！");
        }
        if (ObjectUtil.isNotEmpty(productVo.getLineUpperLimit())){
            if (entity.getQuantity()>productVo.getLineUpperLimit()){
                throw new ServiceException("该商品购物车内仅能添加"+productVo.getLineUpperLimit()+"次");
            }
        }
        //免费商品和积点兑换商品无法加入购物车中
        if (productVo.getPickupMethod().equals("0") || productVo.getPickupMethod().equals("2")){
            throw new ServiceException("免费领取商品和积点兑换商品无法加入购物车，请您直接购买");
        }
        //内容分销商品不支持加入购物车
        if (productVo.getUnionPay().equals("1") || productVo.getProductType().equals("10") || productVo.getProductType().equals("11") ||productVo.getProductType().equals("12")){
            throw new ServiceException("该商品暂不支持加入购物车");
        }
        entity.setCreateSellingPrice(productVo.getSellAmount());
    }

    /**
     * 批量删除购物车
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids,Long userId,Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.delete(new LambdaQueryWrapper<Cart>().in(Cart::getId,ids).eq(Cart::getUserId,userId)) > 0;
    }
}
