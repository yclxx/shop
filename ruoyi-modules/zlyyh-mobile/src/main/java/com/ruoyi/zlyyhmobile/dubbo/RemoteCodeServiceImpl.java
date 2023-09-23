package com.ruoyi.zlyyhmobile.dubbo;

import com.ruoyi.system.api.RemoteCodeService;
import com.ruoyi.zlyyhmobile.service.ICodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteCodeServiceImpl implements RemoteCodeService {
    private final ICodeService codeService;

    @Override
    public Boolean cancellationCode(String codeNo) {
        return codeService.cancellationCode(codeNo);
    }

    @Override
    public Boolean usedCode(String codeNo) {
        return codeService.usedCode(codeNo);
    }

    @Override
    public Boolean rollbackCode(String codeNo) {
        return codeService.rollbackCode(codeNo);
    }
}
