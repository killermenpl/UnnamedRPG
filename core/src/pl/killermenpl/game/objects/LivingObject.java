package pl.killermenpl.game.objects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import pl.killermenpl.game.assets.AssetManager;
import pl.killermenpl.game.inventory.Inventory;
import pl.killermenpl.game.renderers.DebugShapeRenderer;

public class LivingObject extends GameObject {
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	protected HashMap<String, Object> stats = new HashMap<String, Object>();

	protected float speed = 50;
	protected Vector2 dest;
	public Vector2 dir;
	protected Vector2 vel;
	protected Vector2 mov;
	protected Direction facing = Direction.UP;

	protected ITempAI action;

	protected static NinePatchDrawable hpBar, blackBar;

	public Inventory inventory;

	public Cone cone = new Cone(this);

	public LivingObject(String name, Vector2 pos) {
		super(name, pos);
		dest = pos;
		dir = new Vector2();
		vel = new Vector2();
		mov = new Vector2();
	}

	public LivingObject setAction(ITempAI action) {
		this.action = action;
		return this;

	}

	@Override
	public void init() {
		stats.put("hp", 100f);
		stats.put("maxhp", 100f);
		stats.put("invincible", false);
		if (hpBar == null) {
			hpBar = new NinePatchDrawable(new NinePatch(AssetManager.get("hpBar").asSprite(), 0, 0, 0, 0));
			blackBar = new NinePatchDrawable(new NinePatch(AssetManager.get("blackBar").asSprite(),0,0,0,0));
		}
		// cone = new Polygon();
		super.init();
	}

	@Override
	public void render(SpriteBatch batch, float dt) {
		// super.render(batch, dt);
		if (action != null)
			action.action();
		move(dt);
		box.setPosition(pos);
		cone.updateCone();
		// System.out.println(cone.getVertices());
		DebugShapeRenderer.drawShape(cone);
		blackBar.draw(batch, box.getCenter(Vector2.X).x - (0.5f * (float) getStat("maxhp")) / 2, pos.y + 50,
				0.5f*(float) getStat("maxhp"), 3);
		hpBar.draw(batch, box.getCenter(Vector2.X).x - (0.5f * (float) getStat("maxhp")) / 2, pos.y + 50,
				0.5f * (float) getStat("hp"), 3);
	}

	public void setDest(Vector2 destination) {
		dest = destination;
	}

	public void move(float dt) {
		if (dest.dst(pos) <= 1)
			return;
		dir.set(dest).sub(pos).nor();
		vel = new Vector2(dir).scl(speed);
		mov.set(vel).scl(dt);
		pos.add(mov);
	}

	public Object getStat(String name) {
		return stats.get(name);
	}

	public void setStat(String name, Object value) {
		stats.put(name, value);
	}

	public void modStat(String statname, float amount) {
		Object tmp1;
		if ((tmp1 = stats.get("max" + statname)) != null) {
			float tmp2 = (float) tmp1;
			float curr = (float) stats.get(statname);
			if (curr + amount >= tmp2) {
				stats.put(statname, (float) tmp2);
				return;
			}
			if (curr + amount <= 0) {
				stats.put(statname, 0f);
				return;
			}
		}

		float stat = (float) stats.get(statname);
		stat += amount;
		stats.put(statname, stat);
	}

	public void damage(float cDamage) {
		// System.out.println(cDamage);
		if(!(boolean)getStat("invincible"))
		this.modStat("hp", -cDamage);
	}

}
