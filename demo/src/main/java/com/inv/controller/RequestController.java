package com.inv.controller;

import com.inv.model.Request;
import com.inv.model.RequestItem;
import com.inv.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping
    public int createRequest(@RequestBody RequestRequest reqRequest, Principal principal) {
        // ดึง staffId ของ Technician จาก token
        int staffId = Integer.parseInt(principal.getName());
        reqRequest.getRequest().setStaffId(staffId); // ตั้งค่า ID ผู้สร้าง

        return requestService.createRequest(reqRequest.getRequest(), reqRequest.getItems());
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/pending")
    public List<Request> getPendingRequests() {
        return requestService.getPendingRequests();
    }

    @GetMapping("/{requestId}/items")
    public List<RequestItem> getRequestItems(@PathVariable int requestId) {
        return requestService.getItemsByRequestId(requestId);
    }


    @PutMapping("/{id}/approve")
    public void approve(@PathVariable int id, Principal principal) {
        // ดึง approverId ของ Foreman จาก token
        int approverId = Integer.parseInt(principal.getName());
        requestService.approveRequest(id, approverId);
    }

    @PutMapping("/{id}/reject")
    public void reject(@PathVariable int id, Principal principal) {
        // ดึง approverId ของ Foreman จาก token
        int approverId = Integer.parseInt(principal.getName());
        requestService.rejectRequest(id, approverId);
    }


    // เพิ่ม: Endpoint สำหรับดึงรายการ Request ที่พร้อมจะปิด
    @GetMapping("/ready-to-close")
    public List<Request> getReadyToCloseRequests() {
        return requestService.getReadyToCloseRequests();
    }

    // เพิ่ม: Endpoint สำหรับยืนยันการปิด Request
    @PutMapping("/{id}/close")
    public void closeRequest(@PathVariable int id, Principal principal) {
        int staffId = Integer.parseInt(principal.getName());
        requestService.closeRequest(id, staffId);
    }

    // inner class ไม่มีการเปลี่ยนแปลง
    public static class RequestRequest {
        private Request request;
        private List<RequestItem> items;
        public Request getRequest() { return request; }
        public void setRequest(Request request) { this.request = request; }
        public List<RequestItem> getItems() { return items; }
        public void setItems(List<RequestItem> items) { this.items = items; }
    }
}

