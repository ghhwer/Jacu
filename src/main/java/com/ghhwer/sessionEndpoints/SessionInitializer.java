package com.ghhwer.sessionEndpoints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.ghhwer.utils.ApplicationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class SessionInitializer {
    private final int MAX_INACTIVE_SESSIONS_IN_MEM = 8;

    private final AtomicLong counter = new AtomicLong();
    private final ArrayList<SessionHolder> sessionsHolders = new ArrayList<SessionHolder>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(8);

    private SessionHolder getSessionById(int idx){
        Iterator<SessionHolder> iterator = sessionsHolders.iterator();
        while (iterator.hasNext()) {
            SessionHolder holder = iterator.next();
            if (holder.getIdx() == idx) {
                return holder;
            }
        }
        return null;
    }
    private void RemoveDeadSessionsIfAny(){
        ArrayList<SessionHolder> queuedToRemove = new ArrayList<>();
        int i = 0;
        int currentSize = sessionsHolders.size();
        for(int x = 0; x < sessionsHolders.size(); x++){
            SessionHolder holder = sessionsHolders.get(x);
            if (holder.getStatus() == "dead" || holder.getStatus() == "closed") {
                queuedToRemove.add(holder);
                i++;
            }
            if((currentSize - i) <= MAX_INACTIVE_SESSIONS_IN_MEM)
                break;
        }
        for(int y = 0; y < queuedToRemove.size(); y++){
            sessionsHolders.remove(queuedToRemove.get(y));
        }
        return;
    }


    @PostMapping("/new-session")
    @ResponseBody
    public ApplicationResponse newSession(
            @Valid @RequestBody SessionNew connection,
            @RequestParam(name="externalId", required=false, defaultValue="Stranger") String externalIdx
    ) {
        SessionHolder s = new SessionHolder(
                connection,
                counter.incrementAndGet(), externalIdx, threadPool
        );
        sessionsHolders.add(s);
        if(sessionsHolders.size() >= MAX_INACTIVE_SESSIONS_IN_MEM)
            RemoveDeadSessionsIfAny();

        return new ApplicationResponse(
                true,
                new SessionResponse(
                        s.getIdx(), s.getExternalIdx(), s.getStatus(),
                        s.getCommands()
                )
        );
    }

    @GetMapping("/get-running-session")
    @ResponseBody
    public ApplicationResponse runningSession(@RequestParam(name="id", required = true) int idx){
        SessionHolder holder = getSessionById(idx);
        if(holder != null)
            return new ApplicationResponse(true,
                    new SessionResponse(
                            holder.getIdx(), holder.getExternalIdx(), holder.getStatus(),
                            holder.getCommands()
                    )
            );
        return new ApplicationResponse(false,null);
    }

    @PostMapping("/put-new-command")
    @ResponseBody
    public ApplicationResponse putNewCommand(
            @RequestParam(name="id", required = true) int idx,
            @Valid @RequestBody SessionCommand payload
    ){
        SessionHolder holder = getSessionById(idx);
        if(holder == null)
            return new ApplicationResponse(false,null);
        long index = holder.postCommand(payload.getCommand());
        if (index > 0)
            return new ApplicationResponse(
                    true, index
            );
        return new ApplicationResponse(false, "Cannot send command... Session may be closed or dead");
    }

    @DeleteMapping("/close-session")
    @ResponseBody
    public ApplicationResponse closeSession(
            @RequestParam(name="id", required = true) int idx
    ){
        SessionHolder holder = getSessionById(idx);
        if(holder == null)
            return new ApplicationResponse(false,null);
        holder.closeSession();
        return new ApplicationResponse(true,
                new SessionResponse(
                    holder.getIdx(), holder.getExternalIdx(), holder.getStatus(),
                    holder.getCommands()
            )
        );
    }

    @GetMapping("/get-sessions")
    @ResponseBody
    public ApplicationResponse getAllSessions(){
        ArrayList<SessionResponse> sessionResponses = new ArrayList<>();
        Iterator<SessionHolder> iterator = sessionsHolders.iterator();

        while (iterator.hasNext()) {
            SessionHolder holder = iterator.next();
            sessionResponses.add(
                    new SessionResponse(
                            holder.getIdx(), holder.getExternalIdx(), holder.getStatus(), holder.getCommands()
                    )
            );
        }
        return new ApplicationResponse(true,sessionResponses);
    }
}