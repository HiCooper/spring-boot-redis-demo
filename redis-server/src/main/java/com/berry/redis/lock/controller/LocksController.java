/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.berry.redis.lock.controller;

import com.berry.redis.lock.exception.LockExistsException;
import com.berry.redis.lock.exception.LockNotHeldException;
import com.berry.redis.lock.exception.NoSuchLockException;
import com.berry.redis.lock.service.LockService;
import com.berry.redis.lock.model.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dave Syer
 */
@RestController
@RequestMapping("/")
public class LocksController {

    private final LockService service;

    public LocksController(LockService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Lock> locks() {
        return service.findAll();
    }

    @RequestMapping(value = "{name}", method = RequestMethod.POST)
    public Lock create(@PathVariable String name) {
        return service.create(name);
    }

    @RequestMapping(value = "{name}/{value}", method = RequestMethod.DELETE)
    public Map<String, Object> release(@PathVariable String name, @PathVariable String value) {
        if (!service.release(name, value)) {
            throw new NoSuchLockException();
        }
        return Collections.singletonMap("status", "OK");
    }

    @RequestMapping(value = "{name}/{value}", method = RequestMethod.PUT)
    public Lock refresh(@PathVariable String name, @PathVariable String value) {
        return service.refresh(name, value);
    }

    @ExceptionHandler(LockExistsException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> lockExists() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "INVALID");
        body.put("description", "Lock already exists");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchLockException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> noSuchLock() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "INVALID");
        body.put("description", "Lock not found");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LockNotHeldException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> lockNotHeld() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "INVALID");
        body.put("description", "Lock not held (values do not match)");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}
