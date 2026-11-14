package com.jobportal.common.seed;

import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.Skill;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.service.SkillService;
import com.jobportal.seeker.entity.SeekerProfile;
import com.jobportal.seeker.entity.SeekerSkill;
import com.jobportal.seeker.repository.SeekerProfileRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fills an EMPTY database with demo data so the site has something to show:
 *   recruiter@demo.com / Recruiter@123   and   seeker@demo.com / Seeker@123
 * Controlled by app.seed-demo-data in application.yml.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final long LPA = 100_000L; // 1 lakh rupees

    private final UserRepository users;
    private final JobRepository jobs;
    private final SeekerProfileRepository profiles;
    private final SkillService skills;
    private final PasswordEncoder encoder;

    public DemoDataSeeder(UserRepository users, JobRepository jobs, SeekerProfileRepository profiles,
                          SkillService skills, PasswordEncoder encoder) {
        this.users = users;
        this.jobs = jobs;
        this.profiles = profiles;
        this.skills = skills;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0 || jobs.count() > 0) return;

        User recruiter = newUser("Riya Recruiter", "recruiter@demo.com", "Recruiter@123", Role.RECRUITER);
        User seeker = newUser("Sam Seeker", "seeker@demo.com", "Seeker@123", Role.SEEKER);

        SeekerProfile p = new SeekerProfile();
        p.setUser(seeker);
        p.setHeadline("Java Developer | 2 years experience");
        p.setLocation("Pune");
        p.setBio("Backend developer who enjoys building REST APIs with Spring Boot.");
        p.setExpectedSalary(8 * LPA);
        addSkill(p, "Java", 2);
        addSkill(p, "Spring Boot", 2);
        addSkill(p, "SQL", 2);
        profiles.save(p);

        String[][] rows = {
            // title | company | location | remote | minLPA | maxLPA | skills
            {"Java Backend Developer", "Infosys", "Bengaluru", "false", "8", "14", "Java,Spring Boot,SQL,REST APIs"},
            {"Senior Java Engineer", "TCS", "Pune", "false", "15", "24", "Java,Spring Boot,Microservices,AWS"},
            {"Spring Boot Developer", "Wipro", "Hyderabad", "false", "6", "11", "Java,Spring Boot,PostgreSQL"},
            {"Full Stack Developer (React + Java)", "Zoho", "Chennai", "false", "9", "16", "React,Java,Spring Boot,JavaScript"},
            {"Frontend Developer", "Razorpay", "Bengaluru", "true", "10", "18", "React,JavaScript,Tailwind CSS,HTML/CSS"},
            {"React Developer", "Swiggy", "Bengaluru", "false", "12", "22", "React,TypeScript,JavaScript"},
            {"Junior Frontend Developer", "Freshworks", "Chennai", "false", "4", "7", "HTML/CSS,JavaScript,React"},
            {"Python Developer", "Cognizant", "Kolkata", "false", "5", "10", "Python,Django,SQL"},
            {"Data Analyst", "Fractal Analytics", "Mumbai", "false", "6", "12", "Python,SQL,Excel,Power BI"},
            {"Data Scientist", "Mu Sigma", "Bengaluru", "false", "12", "25", "Python,Machine Learning,SQL"},
            {"Machine Learning Engineer", "Flipkart", "Bengaluru", "false", "18", "35", "Python,Machine Learning,AWS,Docker"},
            {"DevOps Engineer", "HCL Technologies", "Noida", "false", "8", "16", "Docker,Kubernetes,AWS,CI/CD"},
            {"Cloud Engineer (AWS)", "Tech Mahindra", "Pune", "true", "10", "20", "AWS,Docker,Linux,Terraform"},
            {"QA Automation Engineer", "Capgemini", "Hyderabad", "false", "5", "10", "Selenium,Java,Testing"},
            {"Manual Tester", "LTIMindtree", "Mumbai", "false", "3", "6", "Testing,SQL"},
            {"Node.js Backend Developer", "Paytm", "Noida", "false", "9", "18", "Node.js,JavaScript,MongoDB,REST APIs"},
            {"MERN Stack Developer", "Dream11", "Mumbai", "true", "10", "20", "MongoDB,Node.js,React,JavaScript"},
            {"Android Developer", "PhonePe", "Bengaluru", "false", "12", "24", "Kotlin,Android,REST APIs"},
            {"iOS Developer", "CRED", "Bengaluru", "false", "15", "28", "Swift,iOS"},
            {"UI/UX Designer", "Zomato", "Gurugram", "false", "7", "15", "Figma,UI/UX"},
            {"Product Manager", "Ola", "Bengaluru", "false", "20", "35", "Product Management,SQL"},
            {"Business Analyst", "Accenture", "Gurugram", "false", "6", "12", "SQL,Excel,Communication"},
            {"Database Administrator", "Oracle India", "Hyderabad", "false", "9", "17", "PostgreSQL,SQL,Linux"},
            {"Backend Engineer (Remote)", "Postman", "Bengaluru", "true", "16", "30", "Java,Spring Boot,Microservices,Docker"},
            {"Software Engineer Intern", "Amazon India", "Hyderabad", "false", "3", "6", "Java,Python,Data Structures"},
            {"Cyber Security Analyst", "Quick Heal", "Pune", "false", "7", "14", "Security,Linux,Networking"},
            {"Technical Support Engineer", "Zendesk India", "Pune", "false", "3", "5", "Communication,SQL,Linux"},
            {"Golang Developer", "Meesho", "Bengaluru", "true", "14", "26", "Go,Microservices,Docker,PostgreSQL"},
            {"Angular Developer", "Mindtree", "Bengaluru", "false", "7", "13", "Angular,TypeScript,JavaScript"},
            {"Digital Marketing Executive", "Nykaa", "Mumbai", "false", "3", "6", "SEO,Communication,Excel"},
            {"Content Writer (Remote)", "Unacademy", "Delhi", "true", "3", "6", "Communication,SEO"},
            {"Full Stack Java Developer", "Persistent Systems", "Nagpur", "false", "8", "15", "Java,Spring Boot,React,PostgreSQL"},
            {"Site Reliability Engineer", "Hotstar", "Mumbai", "true", "18", "32", "Linux,Kubernetes,Python,AWS"},
        };

        int i = 0;
        for (String[] r : rows) {
            Job j = new Job();
            j.setTitle(r[0]);
            j.setCompanyName(r[1]);
            j.setLocation(r[2]);
            j.setRemote(Boolean.parseBoolean(r[3]));
            j.setSalaryMin(Long.parseLong(r[4]) * LPA);
            j.setSalaryMax(Long.parseLong(r[5]) * LPA);
            String skillList = r[6].replace(",", ", ");
            j.setDescription("We are looking for a " + r[0] + " to join " + r[1] + " in " + r[2] + ".\n\n"
                    + "What you will do:\n- Build, test and ship features with the team\n- Review code and improve quality\n"
                    + "- Work closely with product and design\n\nWhat we are looking for:\n- Hands-on experience with "
                    + skillList + "\n- Good communication and a willingness to learn\n\nCompensation: "
                    + r[4] + " - " + r[5] + " LPA, plus benefits.");
            j.setPostedBy(recruiter);
            j.setPostedAt(Instant.now().minus(i * 7L, ChronoUnit.HOURS)); // staggered so "newest first" is visible
            j.getSkills().addAll(skills.findOrCreateAll(Arrays.asList(r[6].split(","))));
            jobs.save(j);
            i++;
        }
        log.info("Seeded demo data: {} jobs, demo logins recruiter@demo.com and seeker@demo.com", rows.length);
    }

    private User newUser(String name, String email, String password, Role role) {
        User u = new User();
        u.setFullName(name);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role);
        return users.save(u);
    }

    private void addSkill(SeekerProfile p, String name, int years) {
        Skill s = skills.findOrCreate(name);
        SeekerSkill ss = new SeekerSkill();
        ss.setProfile(p);
        ss.setSkill(s);
        ss.setYearsExperience(years);
        p.getSkills().add(ss);
    }
}
