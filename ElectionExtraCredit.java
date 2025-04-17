// Amalia Karaman
// Trees and Heaps Assignment (EC)

import java.util.*;
import java.util.stream.Collectors;

public class ElectionExtraCredit {
    private Map<String,Integer> candidates; // map of names to votes
    private PriorityQueue<CandidateVotes> maxHeap; // max heap to track top
    private int totalVotes; // how many votes cast
    private int p; // total votes allowed

    public ElectionExtraCredit(){
        this.candidates=new HashMap<>(); // init map
        this.maxHeap=new PriorityQueue<>((a,b)->{ // init heap by votes
            if(b.votes!=a.votes)return b.votes-a.votes; // more votes = higher
            return a.candidate.compareTo(b.candidate); // tiebreak name
        });
        this.totalVotes=0; // start at 0
        this.p=0; // no vote limit yet
    }

    public void initializeCandidates(List<String> candidatesList){
        candidates.clear(); // reset votes
        maxHeap.clear(); // reset heap
        totalVotes=0; // reset count
        for(String candidate:candidatesList){
            candidates.put(candidate,0); // set all to 0
            maxHeap.offer(new CandidateVotes(candidate,0)); // add to heap
        }
    }

    public void setTotalVotes(int p){
        this.p=p; // set vote cap
    }

    public boolean castVote(String candidate){
        if(!candidates.containsKey(candidate))return false; // skip if not real
        int newVotes=candidates.get(candidate)+1; // add 1 vote
        candidates.put(candidate,newVotes); // update map
        totalVotes++; // inc count
        maxHeap.offer(new CandidateVotes(candidate,newVotes)); // push to heap
        return true;
    }

    public boolean castRandomVote(){
        if(candidates.isEmpty())return false; // no one to vote
        List<String> candidateList=new ArrayList<>(candidates.keySet()); // get list
        String randomCandidate=candidateList.get(new Random().nextInt(candidateList.size())); // pick random
        return castVote(randomCandidate); // cast it
    }

    public boolean rigElection(String candidate){
        if(!candidates.containsKey(candidate))return false; // not valid

        for(String c:candidates.keySet())candidates.put(c,0); // reset all
        totalVotes=0; // reset count

        int otherCount=candidates.size()-1; // rest of candidates
        int riggedVotes=Math.max(p-otherCount,1); // give enough to win
        candidates.put(candidate,riggedVotes); // rig votes
        totalVotes+=riggedVotes; // update count

        for(String c:candidates.keySet()){ // give 1 to rest
            if(!c.equals(candidate)&&totalVotes<p){
                candidates.put(c,1);
                totalVotes++;
            }
        }

        maxHeap.clear(); // clear heap
        for(Map.Entry<String,Integer> entry:candidates.entrySet()){ // rebuild heap
            maxHeap.offer(new CandidateVotes(entry.getKey(),entry.getValue()));
        }
        return true;
    }

    public List<String> getTopKCandidates(int k){
        return candidates.entrySet().stream() // sort map
                .sorted((a,b)->{
                    int cmp=b.getValue().compareTo(a.getValue()); // by votes
                    return cmp!=0?cmp:a.getKey().compareTo(b.getKey()); // then name
                })
                .limit(k) // top k only
                .map(Map.Entry::getKey) // get names
                .collect(Collectors.toList());
    }

    public void auditElection(){
        candidates.entrySet().stream() // go through map
                .sorted((a,b)->{
                    int cmp=b.getValue().compareTo(a.getValue()); // by votes
                    return cmp!=0?cmp:a.getKey().compareTo(b.getKey()); // then name
                })
                .forEach(entry->System.out.println(entry.getKey()+" - "+entry.getValue())); // print
    }

    private static class CandidateVotes {
        String candidate; // name
        int votes; // votes

        public CandidateVotes(String candidate,int votes){
            this.candidate=candidate; // set name
            this.votes=votes; // set votes
        }
    }

    public static void main(String[]args){
        List<String> candidatePool=Arrays.asList( // full list
                "Marcus Fenix","Dominic Santiago","Damon Baird","Cole Train","Anya Stroud",
                "Victor Hoffman","Clayton Carmine","Queen Myrrah","Jinn","Paduk"
        );

        Random rand=new Random(); // rng

        for(int testNumber=1;testNumber<=5;testNumber++){
            ElectionExtraCredit election=new ElectionExtraCredit(); // make election
            int numCandidates=rand.nextInt(5)+3; // 3–7
            int p=rand.nextInt(11)+5; // 5–15
            List<String> shuffled=new ArrayList<>(candidatePool); // copy list
            Collections.shuffle(shuffled); // mix it
            List<String> candidates=shuffled.subList(0,numCandidates); // pick first few

            election.initializeCandidates(candidates); // set them up
            election.setTotalVotes(p); // set p

            System.out.println("=== Randomized Election Test #"+testNumber+" ===");
            System.out.println("Candidates: "+candidates);
            System.out.println("Total votes (p): "+p);

            for(int i=0;i<p;i++)election.castRandomVote(); // vote randomly

            int k=Math.min(3,candidates.size()); // top k
            System.out.println("Top "+k+" candidates after voting: "+election.getTopKCandidates(k));

            String riggedCandidate=candidates.get(rand.nextInt(candidates.size())); // pick 1 to rig
            election.rigElection(riggedCandidate); // rig it
            System.out.println("Election rigged for: "+riggedCandidate);
            System.out.println("Top "+k+" candidates after rigging: "+election.getTopKCandidates(k));

            System.out.println("auditElection():");
            election.auditElection();
            System.out.println("------\n");
        }
    }
}
