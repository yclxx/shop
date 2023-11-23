package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.PromotionTaskBo;
import com.ruoyi.zlyyh.domain.vo.PromotionTaskVo;
import java.util.List;

public interface IPromotionTaskService {
    List<PromotionTaskVo> list(PromotionTaskBo bo);

    PromotionTaskVo getPromotionTask(Long taskId);

    Boolean merchantApprovalInsert(MerchantApprovalBo bo);
}
