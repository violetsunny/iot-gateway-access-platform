package com.ennew.iot.gateway.web.controller;

import com.ennew.iot.gateway.biz.server.cluster.ClusterManager;
import com.ennew.iot.gateway.web.vo.ServerNodeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kdla.framework.dto.MultiResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/cluster")
@RestController
@Tag(name = "集群管理")
public class ClusterInfoController {

    @Autowired
    private ClusterManager clusterManager;

    @GetMapping("/nodes")
    @Operation(summary = "获取集群节点列表")
    public MultiResponse<ServerNodeVo> getServerNodes() {
        List<ServerNodeVo> list = clusterManager.getAllNode()
                .stream().map(ServerNodeVo::new).collect(Collectors.toList());
        return MultiResponse.buildSuccess(list);
    }

}
